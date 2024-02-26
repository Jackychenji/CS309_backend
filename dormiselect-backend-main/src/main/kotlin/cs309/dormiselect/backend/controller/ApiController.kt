package cs309.dormiselect.backend.controller

import cs309.dormiselect.backend.data.LoginDto
import cs309.dormiselect.backend.data.RestResponse
import cs309.dormiselect.backend.data.asRestResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ApiController(
    val authenticationManager: AuthenticationManager,
    val securityContextHolderStrategy: SecurityContextHolderStrategy,
) {
    val securityContextRepository = HttpSessionSecurityContextRepository();

    @GetMapping("/hello")
    fun hello(): RestResponse<String> = RestResponse.success("Hello, world!")

    @PostMapping("/login")
    fun login(
        @RequestBody body: LoginDto,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): RestResponse<Any?> {
        val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(body.username, body.password)
        return runCatching { authenticationManager.authenticate(authenticationRequest) }.map {
            val context = securityContextHolderStrategy.createEmptyContext()
            context.authentication = it
            securityContextHolderStrategy.context = context
            securityContextRepository.saveContext(context, request, response)

            val authorities = it.authorities.toList().mapNotNull { it.authority }
            val role = authorities.lastOrNull()

            object {
                val token = "SUSTech.$role.Auth"
                val userInfo = object {
                    val userId = 0
                    val userName = ""
                    val dashboard = 0
                    val role = listOf("Administrator", "Teacher", "Student")
                }
            }.asRestResponse()
        }.getOrElse {
            RestResponse.fail(401, "Login failed.")
        }
    }
}