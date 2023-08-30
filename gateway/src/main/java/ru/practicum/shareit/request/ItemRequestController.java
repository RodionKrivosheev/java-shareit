package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.ConstRequestHeader;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    ResponseEntity<Object> saveItemRequest(@RequestHeader(ConstRequestHeader.SHARER_USER_ID) Long userId,
                                           @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Create item request by user {}", userId);
        return itemRequestClient.saveItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    ResponseEntity<Object> getAllByUserId(@RequestHeader(ConstRequestHeader.SHARER_USER_ID) Long userId) {
        log.info("Get all user {} item requests", userId);
        return itemRequestClient.getAllByUserId(userId);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllRequests(@RequestHeader(ConstRequestHeader.SHARER_USER_ID) Long userId,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getRequestById(@RequestHeader(ConstRequestHeader.SHARER_USER_ID) Long userId, @PathVariable Long requestId) {
        log.info("Get item request {}", requestId);
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
