package com.example.bookstack_backend.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table( name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "real_name", length=100)
    private String realName;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "profile_picture_url", columnDefinition = "TEXT")
    private String profilePictureUrl;

    @Column(name = "date_created", updatable = false)
    private LocalDateTime dateCreated;

    @Column(name = "is_banned")
    private Boolean isBanned;

    @Column(name = "theme_preference")
    private String themePreference = "light";


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CreditCard> creditCards = new ArrayList<>();

    private BigDecimal balance = BigDecimal.ZERO;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(name = "profile_picture_type")
    private String profilePictureType;

    @Column(name = "api_key", unique = true)
    private String apiKey;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.dateCreated = LocalDateTime.now();
        this.isBanned = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public Boolean getIsBanned() { return isBanned; }
    public void setIsBanned(Boolean isBanned) { this.isBanned = isBanned; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public List<CreditCard> getCreditCards() { return creditCards; }

    public void setCreditCards(List<CreditCard> creditCards) { this.creditCards = creditCards; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getThemePreference() { return themePreference; }
    public void setThemePreference(String themePreference) {this.themePreference = themePreference;}

    public void setProfilePicture(byte[] profilePicture) { this.profilePicture = profilePicture; }

    public byte[] getProfilePicture() {return profilePicture; }

    public void setProfilePictureType(String profilePictureType) { this.profilePictureType = profilePictureType; }
    public String getProfilePictureType() { return profilePictureType; }

    public void setApiKey(String apiKey) {this.apiKey = apiKey; }
    public String getApiKey() {return apiKey; }
}
