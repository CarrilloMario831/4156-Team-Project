package service.models;

import static service.util.DateTimeUtils.FORMATTER;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import service.util.UserRoles;

/** The type User. */
@Getter
@Setter
@NonNull
@Builder
public class User {
  private final UUID userId;
  private String username;
  private UserRoles role;
  private LocalDateTime lastAccess;

  @Override
  public String toString() {
    return "User [userId="
        + userId
        + ", \nusername="
        + username
        + ", \nrole="
        + role
        + "\nlastAccess= "
        + lastAccess.format(FORMATTER)
        + "]";
  }
}
