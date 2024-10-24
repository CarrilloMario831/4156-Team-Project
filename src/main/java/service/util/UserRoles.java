package service.util;

/** Enum for all possible User roles. */
public enum UserRoles {
  /** Admin user roles. */
  ADMIN("ADMIN"),
  /** User user roles. */
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
