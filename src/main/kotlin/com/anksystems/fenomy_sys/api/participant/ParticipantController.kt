package com.anksystems.fenomy_sys.api.participant

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/sys/v1/participant"], produces = ["application/json"])
@ResponseBody
class ParticipantController {

    //fun getAvatar()
}