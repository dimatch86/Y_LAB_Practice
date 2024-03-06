<template>
  <div>
    <div class="page-title">
      <h3>Категории</h3>
    </div>
    <section>
      <div class="row">
        <div class="col s12 m6">
          <div>
            <div class="page-subtitle">
              <h4>Добавить</h4>
            </div>

            <form @submit.prevent="submitHandler">
              <div class="input-field">
                <input
                    id="name"
                    type="text"
                    v-model.trim="type"
                >
                <label for="name">Название</label>
                <span class="helper-text invalid">Введите название</span>
              </div>

              <button class="btn waves-effect waves-light" type="submit">
                Добавить
                <i class="material-icons right">send</i>
              </button>
            </form>
          </div>
        </div>
        
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'addtype',
  data: () => ({
    type: ''
  }),
  methods: {
    async submitHandler() {
      
      const formData = {
        type: this.type
      }
      try {
        await this.$store.dispatch('addtype', formData);
        await this.$store.dispatch('fetchTypes');
        this.$router.push('/record')

      } catch(e) {
        console.log('Ошибка' + e)

      }
      
    }
  }
}
</script>
