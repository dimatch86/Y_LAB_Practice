<template>
    <div>
      <div class="page-title"  >
        <h3>Аудит действий</h3>
        <form @submit.prevent="submitHandler">
            <button class="btn waves-effect waves-light btn-small" type="submit">
        <i class="material-icons">refresh</i>
      </button>

        </form>
        
      </div>
  
      
  
      <section>
        <table>
          <thead>
          <tr>
            <th>Действие</th>
            <th>Пользователь</th>
            <th>Дата</th>
          </tr>
          </thead>
  
          <tbody>
          <tr v-for="item in audit" :key="item.id">
            <td>{{ item.actionMethod }}</td>
            <td>{{item.actionedBy}}</td>
            <td>{{ item.createAt }}</td>
            
          </tr>
          </tbody>
        </table>
      </section>
    </div>
  </template>
  
  <script>
  export default {
    methods: {
        async submitHandler() {
      
      try {
        await this.$store.dispatch('fetchAudit');

      } catch(e) {
        console.log('Ошибка' + e)

      }
      
    }

    },
    async mounted() {
      if(!Object.keys(this.$store.getters.audit).length) {
        await this.$store.dispatch('fetchAudit');
      }
    },
    computed: {
      audit() {
        return this.$store.getters.audit
      }
    }
  }
  </script>