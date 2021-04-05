
<script>
    import ContactCard from './ContactCard.svelte';
    import Box from './Box.svelte';
    import Link from './nested/Link.svelte';

    export let user;
    export let access;

    let links = ['https://svelte.dev/tutorial', 'https://github.com/sveltejs/svelte'];
</script>

<main>
    <ContactCard>
        <span slot="name">
            {user.name}
        </span>

        <span slot="address">
		    {user.address}
	    </span>
    </ContactCard>

    <Box>
        <h2>Links</h2>
        {#if access}
            <p>This is a box. It can contain anything.</p>
            <ul>
                {#each links as link}
                    <li>
                        <Link link={link} />
                    </li>
                {/each}
                {#each user.links as link}
                    <li>
                        <Link link={link} />
                    </li>
                {/each}
            </ul>
        {:else}
            <p>Access denied</p>
        {/if}
    </Box>
</main>

<style>
    a {
        color: red;
        }

    .bold {
        font-weight: bold;
        }
</style>
