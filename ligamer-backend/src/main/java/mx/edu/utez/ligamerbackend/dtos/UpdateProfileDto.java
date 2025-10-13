package mx.edu.utez.ligamerbackend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileDto {
    private String email;
    private String currentPassword;
    private String newPassword;
}
