package cs309.dormiselect.backend.config

import cs309.dormiselect.backend.domain.account.Account
import cs309.dormiselect.backend.domain.account.Administrator
import cs309.dormiselect.backend.domain.account.Student
import cs309.dormiselect.backend.domain.account.Teacher
import cs309.dormiselect.backend.repo.AccountRepo
import cs309.dormiselect.backend.repo.AdministratorRepo
import cs309.dormiselect.backend.repo.StudentRepo
import cs309.dormiselect.backend.repo.TeacherRepo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.stereotype.Service
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
private class Security(
    val accountRepo: AccountRepo,
    val studentRepo: StudentRepo,
    val teacherRepo: TeacherRepo,
    val administratorRepo: AdministratorRepo,
) {
    private fun Account.asUserDetails() = object : UserDetails {
        override fun getAuthorities() = buildSet {
            add(SimpleGrantedAuthority("Account"))

            if (this@asUserDetails is Student || this@asUserDetails is Teacher || this@asUserDetails is Administrator) {
                add(SimpleGrantedAuthority("Student"))
            }

            if (this@asUserDetails is Teacher || this@asUserDetails is Administrator) {
                add(SimpleGrantedAuthority("Teacher"))
            }

            if (this@asUserDetails is Administrator) {
                add(SimpleGrantedAuthority("Administrator"))
            }
        }

        override fun getUsername() = name

        override fun getPassword() = this@asUserDetails.password

        override fun isAccountNonExpired() = true

        override fun isAccountNonLocked() = true

        override fun isCredentialsNonExpired() = true

        override fun isEnabled() = true

        val account = this@asUserDetails
    }

    @Bean
    fun customUserDetailService() = UserDetailsService {
        it.toIntOrNull()?.let {
            (
                    studentRepo.findByStudentId(it)
                        ?: teacherRepo.findByTeacherId(it)
                        ?: administratorRepo.findByAdministratorId(it)
                    )?.let { return@UserDetailsService it.asUserDetails() }
        }

        return@UserDetailsService accountRepo.findByName(it)?.asUserDetails()
            ?: throw UsernameNotFoundException("User $it not found.")
    }

    @Bean
    fun passwordEncoder() = PasswordEncoderFactories.createDelegatingPasswordEncoder()!!

    @Bean
    fun authenticationManager(
        userDetailsService: UserDetailsService,
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)

        return ProviderManager(authenticationProvider)
    }

    @Bean
    fun httpSessionEventPublisher() = HttpSessionEventPublisher()

    @Bean
    fun securityContextHolderStrategy() = SecurityContextHolder.getContextHolderStrategy()!!

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                authorize("/api/account/**", authenticated)
                authorize("/api/student/**", hasAuthority("Student"))
                authorize("/api/teacher/**", hasAuthority("Teacher"))
                authorize("/api/administrator/**", hasAuthority("Administrator"))
                authorize(anyRequest, permitAll)
            }

            csrf {
                csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse()
                csrfTokenRequestHandler = CsrfTokenRequestAttributeHandler()
                if (System.getenv("CSRF_DISABLE") != null) {
                    disable()
                }
            }

            cors {
                configurationSource = UrlBasedCorsConfigurationSource().apply {
                    registerCorsConfiguration("/**", CorsConfiguration().apply {
                        allowedOrigins = listOf("http://10.26.140.45:7777", "http://10.25.6.231:7777")
                        allowedMethods = listOf("GET", "POST")
                        allowedHeaders = listOf("*")
                        allowCredentials = true
                    })
                }
            }

            formLogin {
                //loginPage = "/api/login"
                //permitAll()
            }

            sessionManagement {
                sessionConcurrency {
                    maximumSessions = 1
                }
            }
        }
        return http.build()
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal(expression = "account")
annotation class CurrentAccount

@Service
class SecurityService {
    fun username(): String? {
        val name = SecurityContextHolder.getContext().authentication.name
        if (name === "anonymousUser") {
            return null
        }
        return name
    }
}