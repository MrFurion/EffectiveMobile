package by.effective.mobile.eb.controller;

import by.effective.mobile.eb.constants.SecurityRole;
import by.effective.mobile.eb.dto.request.RequestBlockCardDto;
import by.effective.mobile.eb.dto.request.RequestTransactionDto;
import by.effective.mobile.eb.dto.request.RequestWithdrawFromCard;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    public static final String SUCCESS = "Success";
    private final UserService userService;

    @Operation(summary = "Find cards of the current user", description = "Retrieves a list of cards belonging to the authenticated user. Accessible only to users with ROLE_USER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseFoundCardDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PreAuthorize(SecurityRole.ROLE_USER)
    @GetMapping
    public ResponseEntity<List<ResponseFoundCardDto>> findCardOfUser() {
        List<ResponseFoundCardDto> cardList = userService.findCardsOfUser();
        return ResponseEntity.ok(cardList);
    }

    @Operation(summary = "Block a card", description = "Blocks a card by its ID with a specified reason. Accessible only to users with ROLE_USER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card blocked successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PreAuthorize(SecurityRole.ROLE_USER)
    @GetMapping("/{id}")
    public ResponseEntity<String> blockCard(@PathVariable Long id, @Validated @RequestBody RequestBlockCardDto requestBlockCardDto) {
        userService.blockedCard(requestBlockCardDto, id);
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "Transfer between cards", description = "Performs a transaction between two cards of the authenticated user. Accessible only to users with ROLE_USER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    @PreAuthorize(SecurityRole.ROLE_USER)
    @PatchMapping
    public ResponseEntity<String> transactionBetweenCardsUser(@Validated @RequestBody RequestTransactionDto requestTransactionDto) {
        userService.transferBetweenCards(requestTransactionDto);
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "Add balance to a card", description = "Adds a specified amount to the balance of a card by its ID. Accessible only to users with ROLE_ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance added successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid amount", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PreAuthorize(SecurityRole.ROLE_ADMIN)
    @PutMapping("/{idCard}/{amount}")
    public ResponseEntity<String> addBalance(@PathVariable Long idCard, @PathVariable BigDecimal amount) {
        userService.addBalance(idCard, amount);
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "Withdraw from a card", description = "Withdraws a specified amount from a card. Accessible only to users with ROLE_USER.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Card not found", content = @Content)
    })
    @PreAuthorize(SecurityRole.ROLE_USER)
    @PatchMapping("/withdraws")
    public ResponseEntity<String> withdrawFromCard(@Validated @RequestBody RequestWithdrawFromCard requestWithdrawFromCard) {
        userService.withdrawFromCard(requestWithdrawFromCard);
        return ResponseEntity.ok(SUCCESS);
    }
}
