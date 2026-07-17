package com.refhub.api.domain.reference

import com.refhub.api.domain.reference.dto.ReferenceCreateRequest
import com.refhub.api.domain.reference.dto.ReferenceSearchRequest
import com.refhub.api.domain.tag.Tag
import com.refhub.api.domain.tag.TagRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

@ExtendWith(MockitoExtension::class)
class ReferenceServiceTest {

    @Mock
    lateinit var referenceRepository: ReferenceRepository

    @Mock
    lateinit var tagRepository: TagRepository

    @InjectMocks
    lateinit var referenceService: ReferenceService

    @Test
    fun `search returns paginated results`() {
        val reference = Reference(
            id = 1L,
            title = "Attention Is All You Need",
            summary = "Transformer architecture paper",
            url = "https://arxiv.org/abs/1706.03762",
            source = ReferenceSource.ARXIV,
            author = "Vaswani et al.",
        )
        val page = PageImpl(listOf(reference))

        whenever(referenceRepository.search(isNull(), isNull(), any<Pageable>()))
            .thenReturn(page)

        val result = referenceService.search(ReferenceSearchRequest())

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].title).isEqualTo("Attention Is All You Need")
    }

    @Test
    fun `create saves reference with tags`() {
        val request = ReferenceCreateRequest(
            title = "LangChain Docs",
            url = "https://docs.langchain.com",
            source = ReferenceSource.BLOG,
            tags = listOf("langchain", "llm"),
        )

        whenever(tagRepository.findByNameIn(any()))
            .thenReturn(listOf(Tag(id = 1L, name = "langchain")))
        whenever(tagRepository.save(any<Tag>()))
            .thenReturn(Tag(id = 2L, name = "llm"))
        whenever(referenceRepository.save(any<Reference>()))
            .thenAnswer { invocation ->
                val ref = invocation.getArgument<Reference>(0)
                Reference(
                    id = 1L,
                    title = ref.title,
                    summary = ref.summary,
                    url = ref.url,
                    source = ref.source,
                    author = ref.author,
                )
            }

        val result = referenceService.create(request)

        assertThat(result.title).isEqualTo("LangChain Docs")
        assertThat(result.source).isEqualTo(ReferenceSource.BLOG)
        verify(referenceRepository).save(any<Reference>())
    }
}
