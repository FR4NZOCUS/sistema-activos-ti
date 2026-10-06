package com.unimaq.sistemaactivos.controller;

import com.unimaq.sistemaactivos.model.Rol;
import com.unimaq.sistemaactivos.model.UsuarioSistema;
import com.unimaq.sistemaactivos.repository.RolRepository;
import com.unimaq.sistemaactivos.repository.UsuarioSistemaRepository;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioSistemaController {

    private final UsuarioSistemaRepository usuarioSistemaRepository;
    private final RolRepository rolRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioSistemaController(
            UsuarioSistemaRepository usuarioSistemaRepository,
            RolRepository rolRepository) {

        this.usuarioSistemaRepository = usuarioSistemaRepository;
        this.rolRepository = rolRepository;

        this.passwordEncoder
                = new BCryptPasswordEncoder();
    }


    /*
     * =====================================================
     * LISTADO
     * =====================================================
     */
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {

        model.addAttribute(
                "usuarios",
                usuarioSistemaRepository.findAll()
        );

        return "usuarios";
    }


    /*
     * =====================================================
     * NUEVO USUARIO
     * =====================================================
     */
    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {

        model.addAttribute(
                "usuario",
                new UsuarioSistema()
        );

        model.addAttribute(
                "roles",
                rolRepository.findAll()
        );

        return "nuevo-usuario";
    }


    /*
     * =====================================================
     * GUARDAR NUEVO USUARIO
     * =====================================================
     */
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(
            @RequestParam String nombreUsuario,
            @RequestParam String nombreCompleto,
            @RequestParam(required = false) String correo,
            @RequestParam Integer idRol,
            @RequestParam String password,
            @RequestParam(
                    value = "estado",
                    required = false
            ) Boolean estado) {


        /*
         * Validar nombre de usuario.
         */
        if (nombreUsuario == null
                || nombreUsuario.isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio."
            );
        }


        /*
         * Validar nombre completo.
         */
        if (nombreCompleto == null
                || nombreCompleto.isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre completo es obligatorio."
            );
        }


        /*
         * Validar contraseña.
         */
        if (password == null
                || password.isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria."
            );
        }

        if (password.length() < 6) {

            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 6 caracteres."
            );
        }


        /*
         * Limpiar valores.
         */
        nombreUsuario
                = nombreUsuario.trim();

        nombreCompleto
                = nombreCompleto.trim();

        if (correo != null) {

            correo
                    = correo.trim();

            if (correo.isBlank()) {
                correo = null;
            }
        }


        /*
         * Validar nombre de usuario duplicado.
         */
        if (usuarioSistemaRepository
                .existsByNombreUsuario(
                        nombreUsuario
                )) {

            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese nombre."
            );
        }


        /*
         * Validar correo duplicado.
         */
        if (correo != null
                && usuarioSistemaRepository
                        .existsByCorreo(
                                correo
                        )) {

            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese correo."
            );
        }


        /*
         * Buscar rol.
         */
        Rol rol
                = rolRepository
                        .findById(idRol)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Rol no encontrado."
                                )
                        );


        /*
         * Crear usuario.
         */
        UsuarioSistema usuario
                = new UsuarioSistema();

        usuario.setNombreUsuario(
                nombreUsuario
        );

        usuario.setNombreCompleto(
                nombreCompleto
        );

        usuario.setCorreo(
                correo
        );

        usuario.setRol(
                rol
        );


        /*
         * =================================================
         * CIFRAR CONTRASEÑA CON BCRYPT
         * =================================================
         */
        String passwordHash
                = passwordEncoder.encode(
                        password
                );

        usuario.setPasswordHash(
                passwordHash
        );

        usuario.setEstado(
                estado != null
                        ? estado
                        : false
        );

        usuario.setFechaCreacion(
                LocalDateTime.now()
        );


        /*
         * Guardar.
         */
        usuarioSistemaRepository.save(
                usuario
        );

        return "redirect:/usuarios";
    }


    /*
     * =====================================================
     * EDITAR USUARIO
     * =====================================================
     */
    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(
            @PathVariable Integer id,
            Model model) {

        UsuarioSistema usuario
                = usuarioSistemaRepository
                        .findById(id)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Usuario no encontrado."
                                )
                        );

        model.addAttribute(
                "usuario",
                usuario
        );

        model.addAttribute(
                "roles",
                rolRepository.findAll()
        );

        return "editar-usuario";
    }


    /*
     * =====================================================
     * ACTUALIZAR USUARIO
     * =====================================================
     */
    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(
            @RequestParam Integer idUsuario,
            @RequestParam String nombreUsuario,
            @RequestParam String nombreCompleto,
            @RequestParam(required = false) String correo,
            @RequestParam Integer idRol,
            @RequestParam(required = false) String password,
            @RequestParam(
                    value = "estado",
                    required = false
            ) Boolean estado) {

        UsuarioSistema usuario
                = usuarioSistemaRepository
                        .findById(idUsuario)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Usuario no encontrado."
                                )
                        );


        /*
         * Validaciones básicas.
         */
        if (nombreUsuario == null
                || nombreUsuario.isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio."
            );
        }

        if (nombreCompleto == null
                || nombreCompleto.isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre completo es obligatorio."
            );
        }


        /*
         * Limpiar datos.
         */
        nombreUsuario
                = nombreUsuario.trim();

        nombreCompleto
                = nombreCompleto.trim();

        if (correo != null) {

            correo
                    = correo.trim();

            if (correo.isBlank()) {
                correo = null;
            }
        }


        /*
         * Validar nombre de usuario duplicado.
         */
        final String nombreUsuarioFinal
                = nombreUsuario;

        usuarioSistemaRepository
                .findByNombreUsuario(
                        nombreUsuarioFinal
                )
                .ifPresent(usuarioExistente -> {

                    if (!usuarioExistente
                            .getIdUsuario()
                            .equals(idUsuario)) {

                        throw new IllegalArgumentException(
                                "Ya existe otro usuario con ese nombre."
                        );
                    }
                });


        /*
         * Validar correo duplicado.
         */
        if (correo != null) {

            final String correoFinal
                    = correo;

            usuarioSistemaRepository
                    .findByCorreo(
                            correoFinal
                    )
                    .ifPresent(usuarioExistente -> {

                        if (!usuarioExistente
                                .getIdUsuario()
                                .equals(idUsuario)) {

                            throw new IllegalArgumentException(
                                    "Ya existe otro usuario con ese correo."
                            );
                        }
                    });
        }


        /*
         * Buscar rol.
         */
        Rol rol
                = rolRepository
                        .findById(idRol)
                        .orElseThrow(()
                                -> new IllegalArgumentException(
                                        "Rol no encontrado."
                                )
                        );


        /*
         * Actualizar datos generales.
         */
        usuario.setNombreUsuario(
                nombreUsuario
        );

        usuario.setNombreCompleto(
                nombreCompleto
        );

        usuario.setCorreo(
                correo
        );

        usuario.setRol(
                rol
        );

        usuario.setEstado(
                estado != null
                        ? estado
                        : false
        );


        /*
         * =================================================
         * CAMBIO OPCIONAL DE CONTRASEÑA
         * =================================================
         *
         * Si el campo queda vacío,
         * se conserva el hash actual.
         */
        if (password != null
                && !password.isBlank()) {

            if (password.length() < 6) {

                throw new IllegalArgumentException(
                        "La nueva contraseña debe tener al menos 6 caracteres."
                );
            }

            String nuevoHash
                    = passwordEncoder.encode(
                            password
                    );

            usuario.setPasswordHash(
                    nuevoHash
            );
        }


        /*
         * Guardar cambios.
         */
        usuarioSistemaRepository.save(
                usuario
        );

        return "redirect:/usuarios";
    }
}
