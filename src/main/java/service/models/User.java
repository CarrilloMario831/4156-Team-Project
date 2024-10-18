package service.models;

import static service.util.DateTimeUtils.FORMATTER;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;
import service.util.UserRoles;

/** Sample javadoc for User mini class. */
@Getter
@Setter
@NonNull
@Builder
public class User {
  private final UUID userId;
  private String username;
  private UserRoles role;
  private LocalDateTime lastAccess;

  @Nullable private final UUID inventoryAccess;

  @Override
  public String toString() {
    return "User [userId="
        + userId
        + ", \nusername="
        + username
        + ", \nrole="
        + role
        + ", \ninventoryAccess= "
        + inventoryAccess
        + "\nlastAccess= "
        + lastAccess.format(FORMATTER)
        + "]";
  }
}
