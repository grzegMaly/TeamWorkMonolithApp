package com.mordiniaa.backend.models.team;

import com.mordiniaa.backend.models.BaseEntity;
import com.mordiniaa.backend.models.user.mysql.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Team")
@Table(name = "teams", uniqueConstraints = {
        @UniqueConstraint(name = "uq_team_name", columnNames = "team_name")
})
public class Team extends BaseEntity {

    @Id
    @Column(name = "team_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID teamId;

    @Column(name = "team_name", nullable = false, length = 40)
    private String teamName;

    @Column(name = "presentation_name", nullable = false, length = 40)
    private String presentationName;

    @Column(name = "active")
    private boolean active = true;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
    private User manager;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "teams_users",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> teamMembers = new HashSet<>();

    public Team(String teamName) {
        this.teamName = teamName;
    }

    public void removeManager() {
        this.manager = null;
    }

    public void deactivate() {
        this.active = false;
    }

    public void addMember(User user) {
        this.teamMembers.add(user);
    }

    public void removeMember(User user) {
        this.teamMembers.remove(user);
    }
}
