package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private Long id;
    @Size(max = 300, groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;
    @Size(max = 1000, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class, Update.class})
    private String description;
    @NotNull(groups = {Create.class, Update.class})
    private Boolean available;
    private Long requestId;
}
