package service.util;

/** Enum for all possible User roles. */
public enum UserRoles {
  ADMIN("ADMIN"),
  USER("USER");

  private final String role;

  UserRoles(String role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return role;
  }
}
