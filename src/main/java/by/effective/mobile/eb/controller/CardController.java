package by.effective.mobile.eb.controller;

import by.effective.mobile.eb.constants.SecurityRole;
import by.effective.mobile.eb.dto.request.RequestCreatCardDto;
import by.effective.mobile.eb.dto.request.RequestUpdateLimitDto;
import by.effective.mobile.eb.dto.response.ResponseCardDto;
import by.effective.mobile.eb.dto.response.ResponseFoundCardDto;
import by.effective.mobile.eb.services.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {
    public static final String CARD_CREATED_SUCCESSFULLY = "Card created successfully";
    private final CardService cardService;

    @PreAuthorize(SecurityRole.ROLE_ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseFoundCardDto> findCardById(@PathVariable Long id) {
        ResponseFoundCardDto responseFoundCardDto = cardService.findById(id);
        return ResponseEntity.ok(responseFoundCardDto);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN)
    @GetMapping()
    public ResponseEntity<Page<ResponseFoundCardDto>> findAllCards(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<ResponseFoundCardDto> responseFoundCardDto = cardService.findAllCards(pageable);
        return ResponseEntity.ok(responseFoundCardDto);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<ResponseFoundCardDto> updateLimitCard(@PathVariable Long id,
                                                                @Validated @RequestBody RequestUpdateLimitDto requestUpdateLimitDto) {
        ResponseFoundCardDto responseFoundCardDto = cardService.changeLimit(id, requestUpdateLimitDto);
        return ResponseEntity.ok(responseFoundCardDto);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN)
    @PostMapping
    public ResponseEntity<String> createCard(@Validated @RequestBody RequestCreatCardDto requestCardDto) throws Exception {
        ResponseCardDto responseCardDto = cardService.createCard(requestCardDto);
        URI location = URI.create("/cards/" + responseCardDto.getId());
        return ResponseEntity.created(location).body(CARD_CREATED_SUCCESSFULLY);
    }

    @PreAuthorize(SecurityRole.ROLE_ADMIN)
    @PutMapping("/{id}/status/{action}")
    public ResponseEntity<ResponseCardDto> changeStatusCard(@PathVariable Long id, @PathVariable String action){
        return ResponseEntity.ok(cardService.changeStatusCard(id, action));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCard(@PathVariable Long id){
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
