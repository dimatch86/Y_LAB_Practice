<template>
  <div>
    <div class="page-title">
      <h3>Передать показания</h3>
      
    </div>
    

    <form class="form" @submit.prevent="submitHandler">
      <div>
          <p v-for="(element, index) in types" :key="index">
            <label>
            <input
               class="with-gap"
               name="type"
               type="radio"
               :value="element"
               v-model.trim="type"
             />
            <span style="color:blue">{{ element }}</span>

            </label>
          </p>
      </div>

      <div class="input-field">
        <input
            id="value"
            type="text"
            v-model.trim="value"
        >
        <label for="value">Значение</label>
        <span class="helper-text invalid">amount</span>
      </div>

      <button class="btn waves-effect waves-light" type="submit">
        Передать
        <i class="material-icons right">send</i>
      </button>
    </form>
  </div>
</template>

<script>
export default {
  name: 'send',
  
    data: () => ({
      type: '',
      value: ''
  }),
  methods: {
    async submitHandler() {
      
      const formData = {
        type: this.type,
        value: this.value
      }
      try {
        await this.$store.dispatch('send', formData);
        await this.$store.dispatch('fetchActuals');
        await this.$store.dispatch('fetchHistory');

      } catch(e) {
        console.log('Ошибка' + e)

      }
      
    }
  },
  async mounted() {
    if(!Object.keys(this.$store.getters.types).length) {
      await this.$store.dispatch('fetchTypes');
    }
  },
  computed: {
    types() {
      return this.$store.getters.types
    }
  }
  
}
</script>
