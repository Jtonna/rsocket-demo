package it.dsibilio.rsocketdemo.api.rest;

import it.dsibilio.rsocketdemo.domain.Tweet;
import it.dsibilio.rsocketdemo.domain.TweetRequest;
import it.dsibilio.rsocketdemo.service.TweetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TweetController {

    private final TweetService service;
    private final Mono<RSocketRequester> requester;

    public TweetController(TweetService service, Mono<RSocketRequester> requester) {
        this.service = service;
        this.requester = requester;
    }

    @GetMapping(value = "/tweets/{author}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Tweet> getByAuthor(@PathVariable String author) {
        return service.getByAuthor(author);
    }

    @GetMapping(value = "/socket/{author}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Tweet> getByAuthorViaSocket(@PathVariable String author) {
        return requester.flatMapMany(r -> r.route("tweets.by.author").data(new TweetRequest(author)).retrieveFlux(Tweet.class));
    }

    // helloWorld
    @GetMapping(value = "/helloWorld", produces = {"application/json"})
    public ResponseEntity<?> helloWorld()
    {
        String rtnString = "hello world";
        return new ResponseEntity<>(rtnString, HttpStatus.I_AM_A_TEAPOT);
    }

}
