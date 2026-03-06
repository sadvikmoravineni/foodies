package in.bushansirgur.foodiesapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCartItemRequest {
    @NotBlank
    private String productId;

    @Min(1)
    private int quantity = 1;
}
