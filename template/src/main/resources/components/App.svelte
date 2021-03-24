<script>
    import Inner from './nested/Inner.svelte';

    export let user;
</script>

<main>
    {#if user}
        Hello, {user.name}

        {#each user.links as link}
            <li>
                <a target="_blank" href="{link}">{link}</a>
            </li>
        {/each}
    {:else}
        Access denied
    {/if}
</main>

<Inner />

