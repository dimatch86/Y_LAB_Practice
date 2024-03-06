<template>
  <div>
    <div class="page-title">
      <h3>История записей</h3>
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
        <tr v-for="item in history" :key="item.id">
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
    if(!Object.keys(this.$store.getters.history).length) {
      await this.$store.dispatch('fetchHistory');
    }
  },
  computed: {
    history() {
      return this.$store.getters.history
    }
  }
}
</script>
