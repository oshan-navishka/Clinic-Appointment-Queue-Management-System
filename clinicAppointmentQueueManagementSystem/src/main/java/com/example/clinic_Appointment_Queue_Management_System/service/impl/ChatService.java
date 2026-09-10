package com.example.clinic_Appointment_Queue_Management_System.service.impl;

import com.example.clinic_Appointment_Queue_Management_System.dto.ChatRequest;
import com.example.clinic_Appointment_Queue_Management_System.dto.ChatResponse;
import com.example.clinic_Appointment_Queue_Management_System.entity.Doctor;
import com.example.clinic_Appointment_Queue_Management_System.entity.DoctorAvailability;
import com.example.clinic_Appointment_Queue_Management_System.entity.Specializations;
import com.example.clinic_Appointment_Queue_Management_System.repository.AppointmentFeeRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.ClinicBranchesRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorAvailabilityRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.DoctorRepository;
import com.example.clinic_Appointment_Queue_Management_System.repository.SpecializationsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final DoctorRepository             doctorRepository;
    private final SpecializationsRepository    specializationsRepository;
    private final ClinicBranchesRepository     branchRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final AppointmentFeeRepository     feeRepository;
    private final RestTemplate                 restTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:qwen2.5:3b}")
    private String ollamaModel;

    private static final String[] NON_CLINIC_KEYWORDS = {
        "weather", "news", "sport", "cricket", "football", "movie", "film",
        "recipe", "cook", "stock", "crypto", "bitcoin", "politic", "election",
        "war", "country", "capital", "language", "math", "calculate", "formula",
        "game", "music", "song", "singer", "actor", "actress"
    };

    public ChatResponse chat(ChatRequest request) {
        String userMessage = request.getMessage();
        if (userMessage == null || userMessage.isBlank()) {
            return new ChatResponse("Please type a message.", false);
        }

        if (request.isSearchOnline()) {
            return new ChatResponse(askOllamaGeneral(userMessage), false);
        }

        if (isClearlyNonClinic(userMessage)) {
            return new ChatResponse(
                "I'm trained specifically for ClinicQ — doctors, schedules, fees, and branches. " +
                "This seems unrelated to the clinic. Would you like me to search online for an answer?",
                true
            );
        }

        String context = buildClinicContext();
        String reply   = askOllamaWithContext(userMessage, context);
        return new ChatResponse(reply, false);
    }

    private boolean isClearlyNonClinic(String message) {
        String lower = message.toLowerCase();
        for (String kw : NON_CLINIC_KEYWORDS) {
            if (lower.contains(kw)) return true;
        }
        return false;
    }

    private String buildClinicContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ClinicQ Clinic Information ===\n\n");

        List<Specializations> specs = specializationsRepository.findAll();
        if (!specs.isEmpty()) {
            sb.append("SPECIALIZATIONS:\n");
            specs.forEach(s -> sb.append("  - ").append(s.getName())
                    .append(s.getDescription() != null && !s.getDescription().isBlank()
                            ? ": " + s.getDescription() : "")
                    .append("\n"));
            sb.append("\n");
        }

        branchRepository.findAll().stream()
                .filter(b -> b.getStatus() != null && "ACTIVE".equals(b.getStatus().name()))
                .forEach(b -> {
                    sb.append("BRANCH: ").append(b.getBranchName()).append("\n");
                    if (b.getAddress() != null && !b.getAddress().isBlank())
                        sb.append("  Address: ").append(b.getAddress()).append("\n");
                });
        sb.append("\n");

        List<Doctor> doctors = doctorRepository.findAll();
        for (Doctor d : doctors) {
            String name = "Dr. " + d.getFirstName() + " " + d.getLastName();
            String spec = d.getSpecializations() != null
                    ? d.getSpecializations().getName() : "General";

            sb.append("DOCTOR: ").append(name)
              .append(" | Specialization: ").append(spec).append("\n");

            feeRepository.findByDoctor_DoctorId(d.getDoctorId()).ifPresentOrElse(
                fee -> sb.append("  Consultation Fee: ")
                         .append(fee.getCurrency() != null ? fee.getCurrency() : "LKR")
                         .append(" ").append(fee.getConsultationFee()).append("\n"),
                () -> sb.append("  Consultation Fee: LKR 2500.00 (default)\n")
            );

            List<DoctorAvailability> slots = availabilityRepository
                    .findByDoctor_DoctorIdAndAvailableTrue(d.getDoctorId());
            if (!slots.isEmpty()) {
                sb.append("  Schedule:\n");
                slots.forEach(s -> {
                    String branchInfo = s.getBranch() != null
                            ? " at " + s.getBranch().getBranchName() : "";
                    sb.append("    - ").append(s.getDayOfWeek())
                      .append(" ").append(s.getStartTime())
                      .append(" to ").append(s.getEndTime())
                      .append(branchInfo)
                      .append(" (max ").append(s.getMaxSlots()).append(" patients)\n");
                });
            } else {
                sb.append("  Schedule: Not set yet\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String askOllamaWithContext(String userMessage, String context) {
        String systemPrompt =
            "You are ClinicBot, a helpful assistant for ClinicQ clinic. " +
            "Use ONLY the clinic information below to answer the patient's question. " +
            "Be friendly, clear and concise. " +
            "Never share personal contact details like phone numbers or email addresses. " +
            "If the information needed is not available below, say: " +
            "'I don't have that detail — please contact the clinic directly for more info.'\n\n" +
            context;
        return callOllama(systemPrompt, userMessage);
    }

    private String askOllamaGeneral(String userMessage) {
        String systemPrompt =
            "You are a helpful general-purpose assistant. " +
            "Answer the question clearly and concisely based on your knowledge.";
        return callOllama(systemPrompt, userMessage);
    }

    private String callOllama(String systemPrompt, String userMessage) {
        try {
            ObjectNode body = MAPPER.createObjectNode();
            body.put("model", ollamaModel);
            body.put("stream", false);

            ArrayNode messages = MAPPER.createArrayNode();

            ObjectNode sys = MAPPER.createObjectNode();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            messages.add(sys);

            ObjectNode user = MAPPER.createObjectNode();
            user.put("role", "user");
            user.put("content", userMessage);
            messages.add(user);

            body.set("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(MAPPER.writeValueAsString(body), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                ollamaBaseUrl + "/api/chat",
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode root    = MAPPER.readTree(response.getBody());
                JsonNode content = root.path("message").path("content");
                if (!content.isMissingNode()) {
                    return content.asText();
                }
            }
            return "Sorry, I couldn't get a response. Please try again.";

        } catch (Exception e) {
            log.error("Ollama API call failed: {}", e.getMessage());
            return "I'm currently unavailable. Please try again shortly or contact the clinic directly.";
        }
    }
}
