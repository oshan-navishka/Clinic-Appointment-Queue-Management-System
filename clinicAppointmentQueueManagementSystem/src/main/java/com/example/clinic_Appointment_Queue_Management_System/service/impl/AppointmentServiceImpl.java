package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.*;
import com.example.clinic_Appointment_Queue_Management_System.entity.*;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.AppointmentState;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.BookingSource;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.PaymentStatus;
import com.example.clinic_Appointment_Queue_Management_System.enumaration.Status;
import com.example.clinic_Appointment_Queue_Management_System.repository.AppointmentRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.InvoiceRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.PatientRepository;
import com.example.clinic_Appointment_Queue_Management_System.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

    private static final double DEFAULT_FEE = 2500.00;

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final InvoiceRepository invoiceRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void addAppointment(AppointmentDTO appointmentDTO) {
        appointmentDTO.setBookingSource(BookingSource.ADMIN);
        saveAppointment(appointmentDTO);
    }

    @Override
    @Transactional
    public void bookOnline(AppointmentDTO appointmentDTO) {
        appointmentDTO.setBookingSource(BookingSource.ONLINE);
        saveAppointment(appointmentDTO);
    }

    private void saveAppointment(AppointmentDTO appointmentDTO) {
        log.info("Saving appointment {}", appointmentDTO);

        Patient patient = resolvePatient(appointmentDTO);
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found: " + appointmentDTO.getDoctorId()));

        if (appointmentDTO.getAppointmentDate() == null) {
            throw new RuntimeException("Appointment date is required");
        }

        LocalTime time = appointmentDTO.getAppointmentTime() != null
                ? appointmentDTO.getAppointmentTime()
                : LocalTime.of(9, 0);

        long dailyCount = appointmentRepository.countByDoctor_DoctorIdAndAppointmentDate(
                doctor.getDoctorId(), appointmentDTO.getAppointmentDate());

        long count = appointmentRepository.count();
        String generatedId = String.format("A%03d", count + 1);

        double fee = appointmentDTO.getPaymentAmount() != null
                ? appointmentDTO.getPaymentAmount() : DEFAULT_FEE;

        Appointments appointment = new Appointments();
        appointment.setAppointmentId(generatedId);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setAppointmentTime(time);
        appointment.setAppointmentNumber((int) dailyCount + 1);
        appointment.setReason(appointmentDTO.getReason());
        appointment.setStatus(Status.ACTIVE);
        appointment.setBookingSource(appointmentDTO.getBookingSource());
        appointment.setAppointmentState(AppointmentState.PENDING);
        appointment.setPaymentStatus(PaymentStatus.UNPAID);
        appointment.setPaymentAmount(fee);

        if (appointmentDTO.getBranchId() != null && !appointmentDTO.getBranchId().isBlank()) {
            ClinicBranches branch = entityManager.getReference(ClinicBranches.class, appointmentDTO.getBranchId());
            appointment.setClinicBranches(branch);
        }

        appointmentRepository.save(appointment);
        log.info("Appointment {} saved", generatedId);

        AppointmentDTO savedDto = toDto(appointment);

        createInvoice(appointment, patient, fee);

        String patientUserId = patient.getUser().getUserId();
        notificationService.createNotification(
                patientUserId,
                "Appointment Booked",
                "Your appointment #" + generatedId + " with Dr. " +
                        doctor.getFirstName() + " " + doctor.getLastName() +
                        " on " + appointmentDTO.getAppointmentDate() + " has been confirmed.",
                "APPOINTMENT_BOOKED",
                generatedId
        );

        String patientEmail = patient.getUser().getUserEmail();
        String patientName  = patient.getFirstName() + " " + patient.getLastName();
        if (patientEmail != null && !patientEmail.isBlank()) {
            emailService.sendBookingConfirmation(patientEmail, patientName, savedDto);
        }
    }

    private void createInvoice(Appointments appointment, Patient patient, double fee) {
        try {
            long invCount = invoiceRepository.count();
            String invId  = String.format("INV%04d", invCount + 1);

            Invoice invoice = new Invoice();
            invoice.setInvoiceId(invId);
            invoice.setAppointment(appointment);
            invoice.setPatient(patient);
            invoice.setAmount(fee);
            invoice.setCurrency("LKR");
            invoice.setPaymentMethod("PENDING");
            invoice.setInvoiceStatus("PENDING");
            invoice.setIssuedAt(LocalDateTime.now());
            invoiceRepository.save(invoice);
            log.info("Invoice {} created for appointment {}", invId, appointment.getAppointmentId());
        } catch (Exception e) {
            log.error("Failed to create invoice: {}", e.getMessage());
        }
    }


    @Override
    @Transactional
    public void payAppointment(String appointmentId, String userId) {
        Appointments appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Patient patient = patientRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found for this account"));

        if (!appointment.getPatient().getPatientId().equals(patient.getPatientId())) {
            throw new RuntimeException("You can only pay for your own appointments");
        }
        if (appointment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Appointment is already paid");
        }

        appointment.setPaymentStatus(PaymentStatus.PAID);
        appointment.setPaidAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        invoiceRepository.findByAppointment_AppointmentId(appointmentId).ifPresent(inv -> {
            inv.setInvoiceStatus("PAID");
            inv.setPaymentMethod("ONLINE");
            inv.setPaidAt(LocalDateTime.now());
            invoiceRepository.save(inv);
        });

        notificationService.createNotification(
                userId,
                "Payment Confirmed",
                "Payment of LKR " + appointment.getPaymentAmount() +
                        " for appointment #" + appointmentId + " received.",
                "PAYMENT_DONE",
                appointmentId
        );

        AppointmentDTO dto = toDto(appointment);
        String email = patient.getUser().getUserEmail();
        String name  = patient.getFirstName() + " " + patient.getLastName();
        if (email != null && !email.isBlank()) {
            emailService.sendPaymentConfirmation(email, name, dto);
        }
    }


    @Override
    @Transactional
    public void markChecked(String appointmentId, String userId) {
        Appointments appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Doctor doctor = doctorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found for this account"));

        if (!appointment.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
            throw new RuntimeException("You can only check your own patients");
        }

        appointment.setAppointmentState(AppointmentState.CHECKED);
        appointmentRepository.save(appointment);

        String patientUserId = appointment.getPatient().getUser().getUserId();
        notificationService.createNotification(
                patientUserId,
                "Consultation Complete",
                "Your appointment #" + appointmentId + " with Dr. " +
                        doctor.getFirstName() + " " + doctor.getLastName() + " has been marked as checked.",
                "CHECKED",
                appointmentId
        );
    }

    @Override
    @Transactional
    public void adminConfirmPayment(String appointmentId) {
        Appointments appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));
        if (appointment.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Appointment is already paid");
        }
        appointment.setPaymentStatus(PaymentStatus.PAID);
        appointment.setPaidAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        invoiceRepository.findByAppointment_AppointmentId(appointmentId).ifPresent(inv -> {
            inv.setInvoiceStatus("PAID");
            inv.setPaymentMethod("CASH");
            inv.setPaidAt(LocalDateTime.now());
            invoiceRepository.save(inv);
        });

        String patientUserId = appointment.getPatient().getUser().getUserId();
        notificationService.createNotification(
                patientUserId,
                "Payment Confirmed by Admin",
                "Your payment of LKR " + appointment.getPaymentAmount()
                        + " for appointment #" + appointmentId + " has been confirmed by the clinic administration.",
                "PAYMENT_DONE",
                appointmentId
        );
        log.info("Admin confirmed payment for appointment {}", appointmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAllWithDetails().stream()
                .map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getDoctorWeekAppointments(String userId) {
        Doctor doctor = doctorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        LocalDate today = LocalDate.now();
        LocalDate start = today.with(DayOfWeek.MONDAY);
        LocalDate end   = today.with(DayOfWeek.SUNDAY);
        return appointmentRepository.findDoctorWeek(doctor.getDoctorId(), start, end)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getDoctorAllAppointments(String userId) {
        Doctor doctor = doctorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
        return appointmentRepository.findByDoctorId(doctor.getDoctorId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getMyAppointments(String userId) {
        Patient patient = patientRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return appointmentRepository.findByPatientId(patient.getPatientId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDashboardDTO getDoctorDashboard(String userId) {
        Doctor doctor = doctorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        List<AppointmentDTO> all = appointmentRepository.findByDoctorId(doctor.getDoctorId())
                .stream().map(this::toDto).collect(Collectors.toList());

        LocalDate today     = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = today.with(DayOfWeek.SUNDAY);

        List<AppointmentDTO> thisWeek = all.stream()
                .filter(a -> !a.getAppointmentDate().isBefore(weekStart)
                          && !a.getAppointmentDate().isAfter(weekEnd))
                .collect(Collectors.toList());
        List<AppointmentDTO> pending = all.stream()
                .filter(a -> a.getAppointmentState() == AppointmentState.PENDING)
                .collect(Collectors.toList());
        List<AppointmentDTO> checked = all.stream()
                .filter(a -> a.getAppointmentState() == AppointmentState.CHECKED)
                .collect(Collectors.toList());

        String spec = doctor.getSpecializations() != null
                ? doctor.getSpecializations().getName() : "";

        DoctorDashboardDTO d = new DoctorDashboardDTO();
        d.setDoctorId(doctor.getDoctorId());
        d.setDoctorName((doctor.getFirstName() + " " + doctor.getLastName()).trim());
        d.setSpecializationName(spec);
        d.setAllAppointments(all);
        d.setThisWeekAppointments(thisWeek);
        d.setPendingAppointments(pending);
        d.setCheckedAppointments(checked);
        d.setTotalCount((long) all.size());
        d.setThisWeekCount((long) thisWeek.size());
        d.setPendingCount((long) pending.size());
        d.setCheckedCount((long) checked.size());
        return d;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminOverviewDTO getAdminOverview() {
        List<AppointmentDTO> all     = getAllAppointments();
        List<AppointmentDTO> pending = all.stream()
                .filter(a -> a.getAppointmentState() == AppointmentState.PENDING).collect(Collectors.toList());
        List<AppointmentDTO> checked = all.stream()
                .filter(a -> a.getAppointmentState() == AppointmentState.CHECKED).collect(Collectors.toList());

        List<DoctorDTO>  doctors  = doctorService.getAllDoctors();
        List<PatientDTO> patients = patientService.getAllPatients();

        Map<String, DoctorPatientsDTO> grouped = new LinkedHashMap<>();
        for (DoctorDTO doc : doctors) {
            DoctorPatientsDTO row = new DoctorPatientsDTO();
            row.setDoctorId(doc.getDoctorId());
            row.setDoctorName((doc.getFirstName() + " " + doc.getLastName()).trim());
            row.setSpecializationName(doc.getSpecializationName());
            row.setAppointments(new ArrayList<>());
            row.setPendingAppointments(new ArrayList<>());
            row.setCheckedAppointments(new ArrayList<>());
            grouped.put(doc.getDoctorId(), row);
        }
        for (AppointmentDTO a : all) {
            DoctorPatientsDTO row = grouped.get(a.getDoctorId());
            if (row != null) {
                row.getAppointments().add(a);
                if (a.getAppointmentState() == AppointmentState.PENDING)
                    row.getPendingAppointments().add(a);
                else if (a.getAppointmentState() == AppointmentState.CHECKED)
                    row.getCheckedAppointments().add(a);
            }
        }
        grouped.values().forEach(row -> {
            row.setPendingCount((long) row.getPendingAppointments().size());
            row.setCheckedCount((long) row.getCheckedAppointments().size());
        });

        AdminOverviewDTO overview = new AdminOverviewDTO();
        overview.setDoctorCount((long) doctors.size());
        overview.setPatientCount((long) patients.size());
        overview.setPendingCount((long) pending.size());
        overview.setCheckedCount((long) checked.size());
        overview.setUnpaidCount(all.stream().filter(a -> a.getPaymentStatus() == PaymentStatus.UNPAID).count());
        overview.setDoctors(doctors);
        overview.setPatients(patients);
        overview.setPendingAppointments(pending);
        overview.setCheckedAppointments(checked);
        overview.setAllAppointments(all);
        overview.setDoctorPatients(new ArrayList<>(grouped.values()));
        return overview;
    }


    private AppointmentDTO toDto(Appointments a) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentId(a.getAppointmentId());
        dto.setPatientId(a.getPatient().getPatientId());
        dto.setDoctorId(a.getDoctor().getDoctorId());
        dto.setPatientName(a.getPatient().getFirstName() + " " + a.getPatient().getLastName());
        dto.setDoctorName(a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName());
        dto.setPatientContact(a.getPatient().getContact());
        if (a.getDoctor().getSpecializations() != null)
            dto.setDoctorSpecialization(a.getDoctor().getSpecializations().getName());
        dto.setAppointmentDate(a.getAppointmentDate());
        dto.setAppointmentTime(a.getAppointmentTime());
        dto.setAppointmentNumber(a.getAppointmentNumber());
        dto.setReason(a.getReason());
        dto.setStatus(a.getStatus());
        dto.setBookingSource(a.getBookingSource());
        dto.setAppointmentState(a.getAppointmentState());
        dto.setPaymentStatus(a.getPaymentStatus());
        dto.setPaymentAmount(a.getPaymentAmount());
        dto.setPaidAt(a.getPaidAt());
        if (a.getClinicBranches() != null)
            dto.setBranchId(a.getClinicBranches().getBranchId());
        return dto;
    }


    private Patient resolvePatient(AppointmentDTO dto) {
        if (dto.getPatientId() != null && !dto.getPatientId().isBlank())
            return patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found: " + dto.getPatientId()));
        if (dto.getUserId() != null && !dto.getUserId().isBlank())
            return patientRepository.findByUser_UserId(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException(
                            "Patient profile not found. Please complete patient details first."));
        throw new RuntimeException("Patient is required");
    }
}
