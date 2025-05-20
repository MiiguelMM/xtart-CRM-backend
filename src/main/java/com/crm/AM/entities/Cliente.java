package com.crm.AM.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity //Etiqueta para indicarle a Spring que es una entidad que va a ser representada en la base de datos
@Table(name = "clientes") //Para indicarle a Spring que la entidad se va a llamar clientes en la base de datos , sino coge el nombre por defecto

public class Cliente {

    @Id //Para indicarle a Spring que es la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Para indicarle a Spring que la clave primaria es autoincrementable
    private Long id;

    @Column(name = "nombre", nullable = false) //Para indicarle a Spring que el campo se llama nombre y no puede ser nulo
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "email", unique = true, nullable = false) //Unique para que no se pueda repetir
    private String email;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @CreationTimestamp  //Para indicarle a Spring que rellene automaticamente este campo con la fecha de creación
    private LocalDateTime fechaRegistro;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    public Cliente() {}

    public Cliente(Long id, String nombre, String apellido, String email, String telefono, String direccion,
            String password, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.password = password;
        this.activo = activo;
    }

    //Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    

    
}