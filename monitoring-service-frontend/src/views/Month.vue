<template>
    <div>
      <div class="page-title">
        <h3>Записи за месяц</h3>
        <form @submit.prevent="submitHandler">
            <div class="input-field">
        <input
            id="value"
            type="number" min="1" max="12"
            v-model.trim="month"
        >
        <label for="value">Месяц</label>
        
      </div>

      <button class="btn waves-effect waves-light" type="submit">
        Показать
        <i class="material-icons right">send</i>
      </button>

        </form>
        
      </div>
  
      
  
      <section>
        <table>
          <thead>
          <tr>
            <th>Лицевой счет</th>
            <th>Дата</th>
            <th>Категория</th>
            <th>Значение</th>
          </tr>
          </thead>
  
          <tbody>
          <tr v-for="item in months" :key="item.id">
            <td>{{ item.personalAccount }}</td>
            <td>{{item.sendingDate}}</td>
            <td>{{ item.readingType }}</td>
            <td>{{ item.value }}</td>
            
          </tr>
          </tbody>
        </table>
      </section>
    </div>
  </template>
  
  <script>
  export default {
    name: 'month',
    data: () => ({
    month: ''
  }),
  methods: {
    async submitHandler() {
      const formData = {
        month: this.month
      }
      try {
        await this.$store.dispatch('month', formData);

      } catch(e) {
        console.log('Ошибка' + e)

      }
      
    }

  },
    async mounted() {
      if(!Object.keys(this.$store.getters.months).length) {
        await this.$store.dispatch('fetchMonth');
      }
    },
    computed: {
      months() {
        return this.$store.getters.months
      }
    }
  }
  </script>