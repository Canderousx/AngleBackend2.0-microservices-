package com.commentsManager.app.Controllers;
import com.commentsManager.app.Config.Exceptions.MediaNotFoundException;
import com.commentsManager.app.Models.Comment;
import com.commentsManager.app.Models.Records.ServerMessage;
import com.commentsManager.app.Services.Comments.CommentManagementService;
import com.commentsManager.app.Services.Comments.CommentRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentRetrievalService commentRetrievalService;

    private final CommentManagementService commentManagementService;

    @RequestMapping(value = "/getComment",method = RequestMethod.GET)
    public Comment getComment(@RequestParam String id) throws MediaNotFoundException {
        return commentRetrievalService.getComment(id);
    }

    @RequestMapping(value = "/getVideoComments",method = RequestMethod.GET)
    public Page<Comment>getVideoComments(@RequestParam String id, @RequestParam int page, @RequestParam int pageSize){
        return commentRetrievalService.getVideoComments(id,page,pageSize);
    }


    @RequestMapping(value = "/addComment",method = RequestMethod.POST)
    public ResponseEntity<ServerMessage> addComment(@RequestBody Comment comment) throws IOException{
        commentManagementService.addComment(comment);
        return ResponseEntity.ok().body(new ServerMessage("Comment has been added."));
    }

    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    public ResponseEntity<ServerMessage>deleteComment(@RequestParam String id) throws IOException, MediaNotFoundException, ClassNotFoundException {
        commentManagementService.removeComment(id);
        return ResponseEntity.ok(new ServerMessage("Comment has been deleted."));
    }

}
