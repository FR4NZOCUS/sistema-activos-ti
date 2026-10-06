/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.config;

import com.unimaq.sistemaactivos.service.UsuarioDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(
            UsuarioDetailsService usuarioDetailsService) {

        this.usuarioDetailsService
                = usuarioDetailsService;
    }


    /*
     * =====================================================
     * PASSWORD ENCODER
     * =====================================================
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /*
     * =====================================================
     * PROVEEDOR DE AUTENTICACIÓN
     * =====================================================
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider
                = new DaoAuthenticationProvider(
                        usuarioDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder
        );

        return provider;
    }


    /*
     * =====================================================
     * AUTHENTICATION MANAGER
     * =====================================================
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration
                .getAuthenticationManager();
    }


    /*
     * =====================================================
     * CONFIGURACIÓN GENERAL DE SEGURIDAD
     * =====================================================
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider)
            throws Exception {

        http
                /*
                 * =========================================
                 * PROVEEDOR DE AUTENTICACIÓN
                 * =========================================
                 */
                .authenticationProvider(
                        authenticationProvider
                )
                /*
                 * =========================================
                 * AUTORIZACIÓN
                 * =========================================
                 */
                .authorizeHttpRequests(auth -> auth
                        /*
                         * ---------------------------------
                         * RUTAS PÚBLICAS
                         * ---------------------------------
                         */
                        .requestMatchers(
                                "/login",
                                "/acceso-denegado"
                        )
                        .permitAll()
                        /*
                         * ---------------------------------
                         * GESTIÓN DE USUARIOS
                         *
                         * SOLO ADMINISTRADOR
                         * ---------------------------------
                         *
                         * Incluye:
                         *
                         * /usuarios
                         * /usuarios/nuevo
                         * /usuarios/guardar
                         * /usuarios/editar/**
                         * /usuarios/actualizar
                         */
                        .requestMatchers(
                                "/usuarios/**"
                        )
                        .hasRole(
                                "ADMINISTRADOR"
                        )
                        /*
                         * ---------------------------------
                         * INVENTARIO
                         *
                         * CREACIÓN / EDICIÓN
                         * ---------------------------------
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/inventario/nuevo",
                                "/inventario/editar/**"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        .requestMatchers(
                                HttpMethod.POST,
                                "/inventario/guardar"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        /*
                         * ---------------------------------
                         * COLABORADORES
                         *
                         * CREACIÓN / EDICIÓN
                         * ---------------------------------
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/colaboradores/nuevo",
                                "/colaboradores/editar/**"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        .requestMatchers(
                                HttpMethod.POST,
                                "/colaboradores/guardar"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        /*
                         * ---------------------------------
                         * ASIGNACIONES
                         * ---------------------------------
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/asignaciones/nueva"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        .requestMatchers(
                                HttpMethod.POST,
                                "/asignaciones/guardar"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        /*
                         * ---------------------------------
                         * DEVOLUCIONES
                         *
                         * REGISTRO Y REVISIÓN
                         * ---------------------------------
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/devoluciones/nueva/**",
                                "/devoluciones/revisar/**"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        .requestMatchers(
                                HttpMethod.POST,
                                "/devoluciones/guardar",
                                "/devoluciones/revisar"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        /*
                         * ---------------------------------
                         * REQUISITOS POR CARGO
                         * ---------------------------------
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/requisitos-cargo/nuevo",
                                "/requisitos-cargo/editar/**"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        .requestMatchers(
                                HttpMethod.POST,
                                "/requisitos-cargo/guardar"
                        )
                        .hasAnyRole(
                                "ADMINISTRADOR",
                                "SOPORTE_TI"
                        )
                        /*
                         * ---------------------------------
                         * RESTO DEL SISTEMA
                         *
                         * ADMINISTRADOR
                         * SOPORTE_TI
                         * CONSULTA
                         *
                         * Todos pueden consultar.
                         * ---------------------------------
                         */
                        .anyRequest()
                        .authenticated()
                )
                /*
                 * =========================================
                 * LOGIN
                 * =========================================
                 */
                .formLogin(form -> form
                        .loginPage(
                                "/login"
                        )
                        .loginProcessingUrl(
                                "/login"
                        )
                        .usernameParameter(
                                "username"
                        )
                        .passwordParameter(
                                "password"
                        )
                        .defaultSuccessUrl(
                                "/",
                                true
                        )
                        .failureUrl(
                                "/login?error=true"
                        )
                        .permitAll()
                )
                /*
                 * =========================================
                 * ACCESO DENEGADO
                 * =========================================
                 */
                .exceptionHandling(exception -> exception
                        .accessDeniedPage(
                                "/acceso-denegado"
                        )
                )
                /*
                 * =========================================
                 * LOGOUT
                 * =========================================
                 */
                .logout(logout -> logout
                        .logoutUrl(
                                "/logout"
                        )
                        .logoutSuccessUrl(
                                "/login?logout=true"
                        )
                        .invalidateHttpSession(
                                true
                        )
                        .deleteCookies(
                                "JSESSIONID"
                        )
                        .permitAll()
                );

        return http.build();
    }
}
