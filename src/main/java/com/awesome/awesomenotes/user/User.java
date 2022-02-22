package com.awesome.awesomenotes.user;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import com.awesome.awesomenotes.user.role.ERole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique = true)
    private String email;
    private String password;
    @ElementCollection(targetClass = ERole.class)
    private Set<ERole> roles = new HashSet<>();

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", username='" + getUsername() + "'" +
                ", email='" + getEmail() + "'" +
                ", password='" + getPassword() + "'" +
                ", roles='" + getRoles() + "'" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username)
                && Objects.equals(email, user.email) && Objects.equals(password, user.password)
                && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, roles);
    }

    public User clone() {
        return new User(
                this.getId(),
                this.getUsername(),
                this.getEmail(),
                this.getPassword(),
                new HashSet<>(this.getRoles()));
    }
}
