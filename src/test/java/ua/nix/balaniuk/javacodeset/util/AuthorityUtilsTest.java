package ua.nix.balaniuk.javacodeset.util;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ua.nix.balaniuk.javacodeset.entity.AuthorityEntity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

public class AuthorityUtilsTest {

    @Test
    public void mapToGrantedAuthorities_fromAuthorityEntitySet() {
        AuthorityEntity authorityEntity1 = new AuthorityEntity();
        authorityEntity1.setName("AUTHORITY_FIRST");
        AuthorityEntity authorityEntity2 = new AuthorityEntity();
        authorityEntity2.setName("AUTHORITY_SECOND");

        Collection<? extends GrantedAuthority> expected = List.of(
                new SimpleGrantedAuthority("AUTHORITY_FIRST"),
                new SimpleGrantedAuthority("AUTHORITY_SECOND"));

        Collection<? extends GrantedAuthority> actual = AuthorityUtils.mapToGrantedAuthorities(
                Set.of(authorityEntity1, authorityEntity2));

        assertThat(expected, Matchers.containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void mapToStringList_fromAuthorityEntitySet() {
        AuthorityEntity authorityEntity1 = new AuthorityEntity();
        authorityEntity1.setName("AUTHORITY_FIRST");
        AuthorityEntity authorityEntity2 = new AuthorityEntity();
        authorityEntity2.setName("AUTHORITY_SECOND");

        List<String> expected = List.of("AUTHORITY_FIRST", "AUTHORITY_SECOND");

        List<String> actual = AuthorityUtils.mapToStringList(Set.of(authorityEntity1, authorityEntity2));

        assertThat(expected, Matchers.containsInAnyOrder(actual.toArray()));
    }

    @Test
    public void mapToStringList_fromGrantedAuthorityCollection() {
        Collection<? extends GrantedAuthority> collection = List.of(
                new SimpleGrantedAuthority("AUTHORITY_FIRST"),
                new SimpleGrantedAuthority("AUTHORITY_SECOND"));

        List<String> expected = List.of("AUTHORITY_FIRST", "AUTHORITY_SECOND");

        List<String> actual = AuthorityUtils.mapToStringList(collection);

        assertThat(expected, Matchers.containsInAnyOrder(actual.toArray()));
    }
}
