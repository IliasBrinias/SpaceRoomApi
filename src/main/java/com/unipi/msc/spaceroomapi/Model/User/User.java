package com.unipi.msc.spaceroomapi.Model.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.unipi.msc.spaceroomapi.Model.Image.Image;
import com.unipi.msc.spaceroomapi.Model.Enum.Gender;
import com.unipi.msc.spaceroomapi.Model.Enum.Role;
import com.unipi.msc.spaceroomapi.Model.Message.Message;
import com.unipi.msc.spaceroomapi.Model.User.UserDao.UserDao;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.collections.ArrayStack;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "USER")
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @Column
    private Long Id;
    @Column(name = "email",unique = true)
    private String email;
    @Column(name = "username",unique = true)
    private String username;
    private String password;
    @Column(insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private Role role;
    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.OTHER;
    @Column
    private String firstName;
    @Column
    private String lastName;
    private Long creationDate;
    @Column
    private Long birthday;
    private Boolean isGoogleAccount = false;
    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<UserDao> userDaos = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();

    public User(String email, String username, String password, @NonNull Role role, Gender gender, String firstName, String lastName, Long birthday) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return Id != null && Objects.equals(Id, user.Id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {return true;}
}
