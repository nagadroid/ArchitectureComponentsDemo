package it.codingjam.github.api

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import it.codingjam.github.core.Owner
import it.codingjam.github.core.Repo
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import org.junit.Test
import retrofit2.Response

class GithubRepositoryTest {

    val githubService: GithubService = mock()

    val repository = GithubRepositoryImpl(githubService)

    @Test
    fun search() = runBlocking {
        val header = "<https://api.github.com/search/repositories?q=foo&page=2>; rel=\"next\"," +
                " <https://api.github.com/search/repositories?q=foo&page=34>; rel=\"last\""
        val headers = mapOf("link" to header)

        whenever(githubService.searchRepos(QUERY)).thenReturn(
                async { Response.success(listOf(REPO_1, REPO_2), Headers.of(headers)) })

        val (items, nextPage) = repository.search(QUERY)

        assertThat(items).containsExactly(REPO_1, REPO_2)
        assertThat(nextPage).isEqualTo(2)
    }

    companion object {
        private const val QUERY = "abc"
        val OWNER = Owner("login", "url")
        val REPO_1 = Repo(1, "name", "fullName", "desc", OWNER, 10000)
        val REPO_2 = Repo(2, "name", "fullName", "desc", OWNER, 10000)
    }
}