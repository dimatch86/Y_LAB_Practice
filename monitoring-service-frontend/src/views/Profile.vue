<template>
  <div>
    <div class="page-title">
      <h3>Профиль</h3>
    </div>

    <form class="form">
      <div class="input-field">
        <input
            id="description"
            type="text"
        >
        
        <label for="description">{{name}}</label>
        <span
              class="helper-text invalid">name</span>
              
      </div>
      <div class="input-field">
        
              <input
            id="personalaccount"
            type="text"
        >
        
        <label for="personalaccount">{{id}}</label>
        <span
              class="helper-text invalid">personal account</span>
      </div>
      <div class="input-field">
        
        <input
      id="date"
      type="text"
  >
  
  <label for="date">{{date}}</label>
  <span
        class="helper-text invalid">reg date</span>
</div>

      
    </form>
  </div>
</template>
<script>
export default {
  
  
  computed: {
    name() {
      return this.$store.getters.info.email
    },
    id() {
      return this.$store.getters.info.personalAccount
    },
    date() {
      return this.$store.getters.info.registrationDate
    }

  }
}
</script>
