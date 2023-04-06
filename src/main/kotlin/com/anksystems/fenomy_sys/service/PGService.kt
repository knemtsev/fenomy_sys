package com.anksystems.fenomy_sys.service

import com.anksystems.fenomy_sys.FenomySysApplication
import com.anksystems.fenomy_sys.MyProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment

class PGService(
    @Autowired private val props: MyProperties,
    @Autowired private val env: Environment,
    @Autowired private val log: LogService,
    @Autowired private val app: FenomySysApplication
) {
}