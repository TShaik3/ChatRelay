<script>
    import Logo from './chatRelayIcon.svelte';

    let username = '';
    let password = '';

    function handleLogin() {
        console.log('Logging in with: ', username, password);
        // login logic
    }

    function handleCancel() {
        username = '';
        password = '';
    }
</script>

<div class="login-pane">
    <div class="login-content">
        <div class="icon-box">
            <svelte:component this={Logo} />
        </div>
        <div class="form-box">
            <h1>Sign into your Account</h1>
            <input type="text" placeholder="Username" bind:value={username} />
            <input type="password" placeholder="Password" bind:value={password} />
            <div class="button-row">
                <button on:click={handleLogin}>Login</button>
                <button on:click={handleCancel}>Cancel</button>
            </div>
        </div>
    </div>
</div>

<style>
  :global(body) {
      margin: 0;
      padding: 0;
      height: 100vh;
      background: linear-gradient(325deg,rgba(72, 42, 155, 1) 0%, rgba(237, 83, 83, 1) 100%);
      display: flex;
      justify-content: center;
      align-items: center;
      font-family: sans-serif;
  }

  .login-pane {
    background-color: #ffffffcc;
    width: 600px;
    padding: 30px;
    border-radius: 10px;
    box-shadow: 0 10px 25px rgba(0,0,0,0.2);
    font-family: sans-serif;
  }

  .login-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 20px;
  }

  .icon-box {
    border-radius: 20px;
    padding: 30px;
    text-align: center;
    font-size: 1.5rem;
    flex: 1;
    margin-right: 20px;
  }

  .form-box {
    flex: 2;
  }

  .form-box h1 {
    font: 1.2rem "Inter", sans-serif;
    align-items: center;
    font-weight: bold;
    font-size: 1.5rem;
    margin-bottom: 20px;
  }

  input {
    width: 100%;
    padding: 10px;
    margin-bottom: 15px;
    font-size: 1rem;
    border-radius: 4px;
  }

  .button-row {
    display: flex;
    align-items: center;
    justify-content: space-evenly;
    gap: 15px;
  }

  button {
    font: 1.2rem "Inter", sans-serif;
    font-weight: bold;
    padding: 10px 20px;
    font-size: 1rem;
    /* border: 1px solid black; */
    border-radius: 10px;
    background: white;
    cursor: pointer;
  }

  button:hover {
    background: #f0f0f0;
  }
</style>