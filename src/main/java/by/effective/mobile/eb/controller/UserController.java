package by.effective.mobile.eb.controller;

import by.effective.mobile.eb.dto.request.RequestBlockCardDto;
import by.effective.mobile.eb.dto.request.RequestTransactionDto;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ResponseFoundCardDto>> findCardOfUser() {
        List<ResponseFoundCardDto> cardList = userService.findCardsOfUser();
        return ResponseEntity.ok(cardList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> blockCard(@PathVariable Long id, @Validated @RequestBody RequestBlockCardDto requestBlockCardDto) {
        userService.blockedCard(requestBlockCardDto, id);
        return ResponseEntity.ok("Success");
    }

    @PatchMapping
    public ResponseEntity<String> transactionBetweenCardsUser(@RequestBody RequestTransactionDto requestTransactionDto) {
        userService.transferBetweenCards(requestTransactionDto);
        return ResponseEntity.ok("Success");
    }

    @PutMapping("/{idCard}/{amount}")
    public ResponseEntity<String> addBalance(@PathVariable Long idCard, @PathVariable BigDecimal amount) {
        userService.addBalance(idCard, amount);
        return ResponseEntity.ok("Success");
    }
}
