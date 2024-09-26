package smartcv.auth.model;

public class UserRoleResponse {
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    private String role;

    public UserRoleResponse(String role) {
        this.role = role;
    }

    // Getter and setter for role
}
