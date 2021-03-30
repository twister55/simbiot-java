<style>
    a {
        color: red;
        }

    .bold {
        font-weight: bold;
        }
</style>

<script>
    import Inner from './nested/Inner.svelte';

    export let user;

    let items = ['0', '1', '2', '3'];
</script>

<main>
    <header class="bold">
        <div>
            {#each items as item}
                <a href="#{item}">{item}</a>
            {/each}
        </div>
    </header>

    <section>
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
    </section>
</main>

<Inner text={user.name} />

