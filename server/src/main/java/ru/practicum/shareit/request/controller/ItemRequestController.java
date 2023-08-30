package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.ConstRequestHeader;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDto saveItemRequest(@RequestHeader(ConstRequestHeader.SHARER_SHORT_DTO_USER_ID) Long userId,
                                   @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.saveItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    List<ItemRequestDto> getAllByUserId(@RequestHeader(ConstRequestHeader.SHARER_SHORT_DTO_USER_ID) Long userId) {
        return itemRequestService.getAllByUserId(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDto> getAllRequests(@RequestHeader(ConstRequestHeader.SHARER_SHORT_DTO_USER_ID) Long userId,
                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                        @RequestParam(defaultValue = "20") @Min(1) int size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ItemRequestDto getRequestById(@RequestHeader(ConstRequestHeader.SHARER_SHORT_DTO_USER_ID) Long userId, @PathVariable Long requestId) {
        return itemRequestService.getRequestById(requestId, userId);
    }
}
