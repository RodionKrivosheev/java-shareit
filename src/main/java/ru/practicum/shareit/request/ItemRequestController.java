package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private final String sharerUserId = "X-Sharer-User-Id";

    @PostMapping
    ItemRequestDto saveItemRequest(@RequestHeader(sharerUserId) Long userId,
                                   @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.saveItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    List<ItemRequestDto> getAllByUserId(@RequestHeader(sharerUserId) Long userId) {
        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllRequests(@RequestHeader(sharerUserId) Long userId,
                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                        @RequestParam(defaultValue = "20") @Min(1) int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getRequestById(@RequestHeader(sharerUserId) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
