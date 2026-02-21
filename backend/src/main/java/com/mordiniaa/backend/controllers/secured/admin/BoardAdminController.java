package com.mordiniaa.backend.controllers.secured.admin;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/board")
public class BoardAdminController {

    @PutMapping
    public void setBoardOwner(@RequestParam(name = "u") UUID userId,
                              @RequestParam(name = "t") UUID teamId,
                              @RequestParam(name = "b") String boardId
    ) {

    }
}
