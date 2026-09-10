package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.AppointmentDTO;
import com.example.clinic_Appointment_Queue_Management_System.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${clinic.name:ClinicQ}")
    private String clinicName;

    @Value("${clinic.support.email:support@clinicq.lk}")
    private String supportEmail;

    @Value("${spring.mail.username:noreply@clinicq.lk}")
    private String fromEmail;

    @Override
    @Async
    public void sendBookingConfirmation(String toEmail, String patientName, AppointmentDTO appt) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(clinicName + " — Appointment Confirmed #" + appt.getAppointmentId());

            String html = buildBookingHtml(patientName, appt);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Booking confirmation email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPaymentConfirmation(String toEmail, String patientName, AppointmentDTO appt) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(clinicName + " — Payment Received #" + appt.getAppointmentId());

            String html = buildPaymentHtml(patientName, appt);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Payment confirmation email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String name, String username,
                                  String password, String role) {
        log.info("Sending welcome email to {} for role {}", toEmail, role);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);

            String roleLabel = switch (role) {
                case "PATIENT"     -> "Patient";
                case "DOCTOR"      -> "Doctor";
                case "ADMIN"       -> "Admin";
                case "SUPER_ADMIN" -> "Super Admin";
                default            -> role;
            };

            helper.setSubject(clinicName + " — Your " + roleLabel + " Account Credentials");
            helper.setText(buildWelcomeHtml(name, username, password, roleLabel), true);
            mailSender.send(message);
            log.info("Welcome email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {} — Error: {}", toEmail, e.getMessage(), e);
        }
    }

    private String buildWelcomeHtml(String name, String username, String password, String roleLabel) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family:'Segoe UI',Arial,sans-serif;background:#f0f6fc;margin:0;padding:0">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f6fc;padding:40px 0">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:16px;overflow:hidden;
                                box-shadow:0 4px 24px rgba(11,42,68,.10)">
                    <!-- header -->
                    <tr>
                      <td style="background:#0b2a44;padding:32px 40px;text-align:center">
                        <h1 style="color:#fff;margin:0;font-size:24px;font-weight:700">%s</h1>
                        <p style="color:rgba(255,255,255,.75);margin:6px 0 0;font-size:14px">
                            Welcome to the Clinic Appointment Queue System
                        </p>
                      </td>
                    </tr>
                    <!-- body -->
                    <tr>
                      <td style="padding:36px 40px">
                        <p style="font-size:16px;color:#1e293b;margin:0 0 16px">
                          Dear <strong>%s</strong>,
                        </p>
                        <p style="font-size:15px;color:#374151;margin:0 0 24px">
                          Your <strong>%s</strong> account has been created successfully.
                          Here are your login credentials:
                        </p>

                        <!-- credentials box -->
                        <div style="background:#f0f9ff;border:1.5px solid #bfdbfe;border-radius:12px;
                                    padding:20px 24px;margin-bottom:24px">
                          <table width="100%%" cellpadding="0" cellspacing="0">
                            <tr>
                              <td style="padding:8px 0;font-size:13px;font-weight:700;
                                         color:#64748b;text-transform:uppercase;letter-spacing:.5px;
                                         width:40%%">Login URL</td>
                              <td style="padding:8px 0;font-size:14px;color:#0b2a44">
                                <a href="http://localhost:8080/login.html"
                                   style="color:#1f6e9c">Click here to login</a>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:8px 0;font-size:13px;font-weight:700;
                                         color:#64748b;text-transform:uppercase;letter-spacing:.5px">
                                  Username
                              </td>
                              <td style="padding:8px 0">
                                <code style="background:#e0f2fe;color:#0b2a44;padding:4px 12px;
                                             border-radius:8px;font-size:15px;font-weight:700">
                                  %s
                                </code>
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:8px 0;font-size:13px;font-weight:700;
                                         color:#64748b;text-transform:uppercase;letter-spacing:.5px">
                                  Password
                              </td>
                              <td style="padding:8px 0">
                                <code style="background:#fef3c7;color:#92400e;padding:4px 12px;
                                             border-radius:8px;font-size:15px;font-weight:700">
                                  %s
                                </code>
                              </td>
                            </tr>
                          </table>
                        </div>

                        <!-- security note -->
                        <div style="background:#fef3c7;border:1px solid #fcd34d;border-radius:10px;
                                    padding:12px 16px;margin-bottom:20px;font-size:13px;color:#92400e">
                          <strong>&#x26A0; Security Notice:</strong>
                          Please change your password after your first login.
                          Do not share your credentials with anyone.
                        </div>

                        <p style="font-size:14px;color:#64748b;margin:0">
                          For help, contact us at
                          <a href="mailto:%s" style="color:#1f6e9c">%s</a>
                        </p>
                      </td>
                    </tr>
                    <!-- footer -->
                    <tr>
                      <td style="background:#f8fafc;padding:20px 40px;text-align:center;
                                 font-size:12px;color:#94a3b8;border-top:1px solid #e2eaf2">
                        © %s &nbsp;·&nbsp; All rights reserved
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(
                clinicName,
                name,
                roleLabel,
                username,
                password,
                supportEmail, supportEmail,
                clinicName
        );
    }

    private String buildBookingHtml(String patientName, AppointmentDTO a) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family:'Segoe UI',Arial,sans-serif;background:#f0f6fc;margin:0;padding:0">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f6fc;padding:40px 0">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:16px;overflow:hidden;
                                box-shadow:0 4px 24px rgba(11,42,68,.10)">
                    <!-- header -->
                    <tr>
                      <td style="background:#0b2a44;padding:32px 40px;text-align:center">
                        <h1 style="color:#fff;margin:0;font-size:24px;font-weight:700">%s</h1>
                        <p style="color:rgba(255,255,255,.7);margin:6px 0 0;font-size:14px">Appointment Confirmation</p>
                      </td>
                    </tr>
                    <!-- body -->
                    <tr>
                      <td style="padding:36px 40px">
                        <p style="font-size:16px;color:#1e293b;margin:0 0 20px">
                          Dear <strong>%s</strong>,
                        </p>
                        <p style="font-size:15px;color:#374151;margin:0 0 24px">
                          Your appointment has been successfully booked. Here are your details:
                        </p>
                        <!-- details card -->
                        <table width="100%%" cellpadding="12" cellspacing="0"
                               style="background:#f8fafc;border-radius:12px;border:1px solid #e2eaf2;
                                      font-size:14px;color:#374151">
                          <tr>
                            <td style="border-bottom:1px solid #e2eaf2;font-weight:600;color:#64748b;
                                       text-transform:uppercase;font-size:12px;letter-spacing:.5px">Field</td>
                            <td style="border-bottom:1px solid #e2eaf2;font-weight:600;color:#64748b;
                                       text-transform:uppercase;font-size:12px;letter-spacing:.5px">Details</td>
                          </tr>
                          <tr>
                            <td style="border-bottom:1px solid #f1f5f9;color:#64748b">Appointment ID</td>
                            <td style="border-bottom:1px solid #f1f5f9;font-weight:600;color:#0b2a44">%s</td>
                          </tr>
                          <tr>
                            <td style="border-bottom:1px solid #f1f5f9;color:#64748b">Doctor</td>
                            <td style="border-bottom:1px solid #f1f5f9;color:#1e293b">Dr. %s</td>
                          </tr>
                          <tr>
                            <td style="border-bottom:1px solid #f1f5f9;color:#64748b">Specialization</td>
                            <td style="border-bottom:1px solid #f1f5f9;color:#1e293b">%s</td>
                          </tr>
                          <tr>
                            <td style="border-bottom:1px solid #f1f5f9;color:#64748b">Date</td>
                            <td style="border-bottom:1px solid #f1f5f9;color:#1e293b">%s</td>
                          </tr>
                          <tr>
                            <td style="border-bottom:1px solid #f1f5f9;color:#64748b">Queue Number</td>
                            <td style="border-bottom:1px solid #f1f5f9;font-weight:700;color:#1d6fa4;font-size:18px">#%s</td>
                          </tr>
                          <tr>
                            <td style="color:#64748b">Consultation Fee</td>
                            <td style="font-weight:600;color:#16a34a">LKR %.2f</td>
                          </tr>
                        </table>
                        <!-- payment notice -->
                        <div style="background:#fef3c7;border:1px solid #fcd34d;border-radius:10px;
                                    padding:14px 18px;margin:24px 0;font-size:14px;color:#92400e">
                          <strong>⚠ Payment Pending</strong> — Please complete your payment to confirm the appointment.
                        </div>
                        <p style="font-size:14px;color:#64748b;margin:0">
                          If you have any questions, contact us at
                          <a href="mailto:%s" style="color:#1d6fa4">%s</a>.
                        </p>
                      </td>
                    </tr>
                    <!-- footer -->
                    <tr>
                      <td style="background:#f8fafc;padding:20px 40px;text-align:center;
                                 font-size:12px;color:#94a3b8;border-top:1px solid #e2eaf2">
                        © %s · All rights reserved
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(
                clinicName,
                patientName,
                a.getAppointmentId(),
                a.getDoctorName() != null ? a.getDoctorName() : "—",
                a.getDoctorSpecialization() != null ? a.getDoctorSpecialization() : "—",
                a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : "—",
                a.getAppointmentNumber() != null ? a.getAppointmentNumber().toString() : "—",
                a.getPaymentAmount() != null ? a.getPaymentAmount() : 2500.0,
                supportEmail, supportEmail,
                clinicName
        );
    }

    private String buildPaymentHtml(String patientName, AppointmentDTO a) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family:'Segoe UI',Arial,sans-serif;background:#f0f6fc;margin:0;padding:0">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f6fc;padding:40px 0">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:16px;overflow:hidden;
                                box-shadow:0 4px 24px rgba(11,42,68,.10)">
                    <tr>
                      <td style="background:#16a34a;padding:32px 40px;text-align:center">
                        <h1 style="color:#fff;margin:0;font-size:24px;font-weight:700">%s</h1>
                        <p style="color:rgba(255,255,255,.8);margin:6px 0 0;font-size:14px">✓ Payment Confirmed</p>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:36px 40px">
                        <p style="font-size:16px;color:#1e293b;margin:0 0 16px">
                          Dear <strong>%s</strong>, your payment has been received!
                        </p>
                        <table width="100%%" cellpadding="12" cellspacing="0"
                               style="background:#f8fafc;border-radius:12px;border:1px solid #e2eaf2;font-size:14px">
                          <tr>
                            <td style="color:#64748b;border-bottom:1px solid #f1f5f9">Appointment ID</td>
                            <td style="font-weight:600;color:#0b2a44;border-bottom:1px solid #f1f5f9">%s</td>
                          </tr>
                          <tr>
                            <td style="color:#64748b;border-bottom:1px solid #f1f5f9">Doctor</td>
                            <td style="color:#1e293b;border-bottom:1px solid #f1f5f9">Dr. %s</td>
                          </tr>
                          <tr>
                            <td style="color:#64748b;border-bottom:1px solid #f1f5f9">Date</td>
                            <td style="color:#1e293b;border-bottom:1px solid #f1f5f9">%s</td>
                          </tr>
                          <tr>
                            <td style="color:#64748b">Amount Paid</td>
                            <td style="font-weight:700;color:#16a34a;font-size:18px">LKR %.2f</td>
                          </tr>
                        </table>
                        <p style="font-size:14px;color:#64748b;margin:24px 0 0">
                          Questions? Email <a href="mailto:%s" style="color:#1d6fa4">%s</a>
                        </p>
                      </td>
                    </tr>
                    <tr>
                      <td style="background:#f8fafc;padding:20px 40px;text-align:center;
                                 font-size:12px;color:#94a3b8;border-top:1px solid #e2eaf2">
                        © %s · All rights reserved
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(
                clinicName,
                patientName,
                a.getAppointmentId(),
                a.getDoctorName() != null ? a.getDoctorName() : "—",
                a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : "—",
                a.getPaymentAmount() != null ? a.getPaymentAmount() : 2500.0,
                supportEmail, supportEmail,
                clinicName
        );
    }
}
