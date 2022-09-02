package blogtest.Payload.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTTokenSucceccResponse {
    private boolean success;
    private String token;
}
