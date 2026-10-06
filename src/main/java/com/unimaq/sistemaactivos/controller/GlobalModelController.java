/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.controller;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelController {


    /*
     * =====================================================
     * USUARIO AUTENTICADO
     * =====================================================
     */
    @ModelAttribute("usuarioAutenticado")
    public String usuarioAutenticado(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return null;
        }

        return authentication.getName();
    }


    /*
     * =====================================================
     * ADMINISTRADOR
     * =====================================================
     */
    @ModelAttribute("esAdministrador")
    public boolean esAdministrador(
            Authentication authentication) {

        return tieneRol(
                authentication,
                "ROLE_ADMINISTRADOR"
        );
    }


    /*
     * =====================================================
     * SOPORTE TI
     * =====================================================
     */
    @ModelAttribute("esSoporte")
    public boolean esSoporte(
            Authentication authentication) {

        return tieneRol(
                authentication,
                "ROLE_SOPORTE_TI"
        );
    }


    /*
     * =====================================================
     * CONSULTA
     * =====================================================
     */
    @ModelAttribute("esConsulta")
    public boolean esConsulta(
            Authentication authentication) {

        return tieneRol(
                authentication,
                "ROLE_CONSULTA"
        );
    }


    /*
     * =====================================================
     * ADMINISTRADOR O SOPORTE
     * =====================================================
     *
     * Lo utilizaremos mucho para botones como:
     *
     * Nuevo
     * Editar
     * Asignar
     * Devolver
     */
    @ModelAttribute("puedeGestionar")
    public boolean puedeGestionar(
            Authentication authentication) {

        return tieneRol(
                authentication,
                "ROLE_ADMINISTRADOR"
        )
                || tieneRol(
                        authentication,
                        "ROLE_SOPORTE_TI"
                );
    }


    /*
     * =====================================================
     * MÉTODO AUXILIAR
     * =====================================================
     */
    private boolean tieneRol(
            Authentication authentication,
            String rol) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return false;
        }

        Collection<? extends GrantedAuthority> autoridades
                = authentication.getAuthorities();

        for (GrantedAuthority autoridad : autoridades) {

            if (rol.equals(
                    autoridad.getAuthority())) {

                return true;
            }
        }

        return false;
    }
}
