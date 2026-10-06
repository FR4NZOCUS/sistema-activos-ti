/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.unimaq.sistemaactivos.repository;

import com.unimaq.sistemaactivos.model.UsuarioSistema;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioSistemaRepository
        extends JpaRepository<UsuarioSistema, Integer> {

    Optional<UsuarioSistema>
            findByNombreUsuario(String nombreUsuario);

    Optional<UsuarioSistema>
            findByCorreo(String correo);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByCorreo(String correo);
}
