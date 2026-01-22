package contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import mvvm.models.Client;
import mvvm.models.Role;

public class UserDto {
    @JsonProperty("id")
    private int id;
    @JsonProperty("username")
    private String username;
    @JsonProperty("role")
    private String role;

    public UserDto() {}

    public UserDto(int id, String username, String role) {
        this.username = username;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
