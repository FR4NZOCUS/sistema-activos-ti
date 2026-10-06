/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.service;

import com.unimaq.sistemaactivos.model.UsuarioSistema;
import com.unimaq.sistemaactivos.repository.UsuarioSistemaRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService
        implements UserDetailsService {

    private final UsuarioSistemaRepository usuarioSistemaRepository;

    public UsuarioDetailsService(
            UsuarioSistemaRepository usuarioSistemaRepository) {

        this.usuarioSistemaRepository
                = usuarioSistemaRepository;
    }


    /*
     * =====================================================
     * CARGAR USUARIO PARA SPRING SECURITY
     * =====================================================
     */
    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        UsuarioSistema usuario
                = usuarioSistemaRepository
                        .findByNombreUsuario(username)
                        .orElseThrow(()
                                -> new UsernameNotFoundException(
                                        "Usuario no encontrado."
                                )
                        );


        /*
         * Validar que exista rol.
         */
        if (usuario.getRol() == null
                || usuario.getRol().getNombre() == null) {

            throw new UsernameNotFoundException(
                    "El usuario no tiene un rol asignado."
            );
        }


        /*
         * Spring Security trabaja mejor con
         * autoridades tipo ROLE_xxx.
         *
         * Ejemplo:
         *
         * ADMINISTRADOR
         * →
         * ROLE_ADMINISTRADOR
         */
        String autoridad
                = "ROLE_"
                + usuario
                        .getRol()
                        .getNombre();


        /*
         * Estado del usuario.
         *
         * true  = habilitado
         * false = deshabilitado
         */
        boolean habilitado
                = Boolean.TRUE.equals(
                        usuario.getEstado()
                );

        return new User(
                usuario.getNombreUsuario(),
                usuario.getPasswordHash(),
                habilitado,
                true,
                true,
                true,
                List.of(
                        new SimpleGrantedAuthority(
                                autoridad
                        )
                )
        );
    }
}
