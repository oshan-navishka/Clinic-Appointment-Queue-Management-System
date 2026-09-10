package com.example.clinic_Appointment_Queue_Management_System.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/patients/selfRegister").permitAll()
                        .requestMatchers("/", "/index.html", "/*.html", "/css/**", "/js/**", "/images/**", "/error").permitAll()

                        // User management
                        .requestMatchers(HttpMethod.POST,   "/api/users/register").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/users/allUsers").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/users/updateUsers").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/deleteUser/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/users/resetPassword").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/users/changeCredentials").authenticated()
                        // Admin overview
                        .requestMatchers(HttpMethod.GET,    "/api/appointments/overview").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        // Doctor management — SUPER_ADMIN only
                        .requestMatchers(HttpMethod.POST,   "/api/doctors/addDct").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/doctors/updateDoctor").hasRole("SUPER_ADMIN")
                        // Specializations — SUPER_ADMIN only
                        .requestMatchers(HttpMethod.POST,   "/api/specializations/saveSpecialization").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/specializations/updateSpecialization").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/specializations/deleteSpecialization/**").hasRole("SUPER_ADMIN")
                        // Clinic branches — SUPER_ADMIN only
                        .requestMatchers(HttpMethod.POST,   "/api/clinicBranches/addBranch").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/clinicBranches/updateBranch").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/clinicBranches/deleteBranch/**").hasRole("SUPER_ADMIN")
                        // Doctor availability & fees — SUPER_ADMIN only
                        .requestMatchers(HttpMethod.POST,   "/api/availability/save").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/availability/**").hasRole("SUPER_ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/fees/save").hasRole("SUPER_ADMIN")

                        //  ADMIN + SUPER_ADMIN
                        .requestMatchers(HttpMethod.GET,    "/api/doctors/loadAllDct").authenticated()

                        // Patient management
                        .requestMatchers(HttpMethod.POST,   "/api/patients/savePatient").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/patients/allPatients").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/patients/updatePatient").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/patients/deletePatient/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/patients/searchPatients").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        // Admin books appointment
                        .requestMatchers(HttpMethod.POST,   "/api/appointments/addAppointment").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        // Admin confirm payment
                        .requestMatchers(HttpMethod.POST,   "/api/appointments/adminConfirmPayment/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        // All appointments list
                        .requestMatchers(HttpMethod.GET,    "/api/appointments/all").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        // Read-only data
                        .requestMatchers(HttpMethod.GET,    "/api/clinicBranches/getAllBranches").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/api/availability/**").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/api/fees/**").authenticated()
                        // Patient medications approval
                        .requestMatchers(HttpMethod.POST,   "/api/medications/save").hasAnyRole("SUPER_ADMIN", "ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT,    "/api/medications/updateStatus/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT,    "/api/medications/approve/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/medications/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/medications/pendingApproval").hasAnyRole("SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/medications/byPatient/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.GET,    "/api/medications/myMedications").hasRole("PATIENT")
                        // Prescriptions
                        .requestMatchers(HttpMethod.POST,   "/api/prescriptions/save").hasAnyRole("DOCTOR", "SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/prescriptions/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "DOCTOR")
                        // Medical Records
                        .requestMatchers(HttpMethod.POST,   "/api/medicalRecords/save").hasAnyRole("DOCTOR", "SUPER_ADMIN", "ADMIN")
                        .requestMatchers(HttpMethod.GET,    "/api/medicalRecords/byPatient/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.GET,    "/api/medicalRecords/byAppointment/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.GET,    "/api/medicalRecords/myRecords").hasRole("PATIENT")

                        // DOCTOR only
                        // Doctor dashboard, weekly & all appointment views, mark-checked
                        .requestMatchers(HttpMethod.GET,    "/api/doctors/dashboard").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET,    "/api/appointments/doctorWeek").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.GET,    "/api/appointments/doctorAll").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PUT,    "/api/appointments/markChecked/**").hasRole("DOCTOR")

                        // PATIENT only
                        // Online booking, payment, own appointments
                        .requestMatchers(HttpMethod.POST,   "/api/appointments/bookOnline").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.POST,   "/api/appointments/pay/**").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.GET,    "/api/appointments/myAppointments").hasRole("PATIENT")

                        // AUTHENTICATED (any logged-in role)
                        // Profile look-ups used by all roles after login
                        .requestMatchers(HttpMethod.GET,    "/api/patients/byUser/**").authenticated()
                        .requestMatchers(HttpMethod.GET,    "/api/doctors/byUser/**").authenticated()
                        // Specializations list needed by booking forms for all roles
                        .requestMatchers(HttpMethod.GET,    "/api/specializations/getAllSpecializations").authenticated()
                        // Doctor cards for patient booking page
                        .requestMatchers(HttpMethod.GET,    "/api/doctors/cards").authenticated()
                        // Ratings — any authenticated user can read; only PATIENT can write
                        .requestMatchers(HttpMethod.GET,    "/api/ratings/**").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/ratings/save").hasRole("PATIENT")
                        // Notifications — own user only (enforced in service layer)
                        .requestMatchers("/api/notifications/**").authenticated()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
