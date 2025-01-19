package com.example.immovision.controllers.review;

import com.example.immovision.dto.ReviewDTO;
import com.example.immovision.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<ReviewDTO>> getPropertyReviews(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(reviewService.getReviewsByPropertyId(propertyId));
    }

    @GetMapping("/property/{propertyId}/rating")
    public ResponseEntity<Double> getPropertyAverageRating(@PathVariable UUID propertyId) {
        return ResponseEntity.ok(reviewService.getPropertyAverageRating(propertyId));
    }

    @GetMapping("/agent/{agentEmail}/rating")
    public ResponseEntity<Double> getAgentOverallRating(@PathVariable String agentEmail) {
        return ResponseEntity.ok(reviewService.getAgentOverallRating(agentEmail));
    }

    @GetMapping("/agent/{agentEmail}/count")
    public ResponseEntity<Long> getAgentReviewCount(@PathVariable String agentEmail) {
        return ResponseEntity.ok(reviewService.getAgentReviewCount(agentEmail));
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(reviewDTO));
    }
}
