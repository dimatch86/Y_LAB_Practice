<template>
    <div>
      <div class="page-title">
        <h3>Актуальные записи</h3>
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
          <tr v-for="item in actuals" :key="item.id">
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
    async mounted() {
      if(!Object.keys(this.$store.getters.actuals).length) {
        await this.$store.dispatch('fetchActuals');
      }
    },
    computed: {
      actuals() {
        return this.$store.getters.actuals
      }
    }
  }
  </script>