<template>
  <div class="grey darken-1 empty-layout">
    <router-view />
  </div>
</template>

<script>
export default {
  computed: {
    error() {
      return this.$store.getters.error
    }
  },
  watch: {
    error(e) {
      console.log('ERROR!!' + e.message)
    }
  }
}
</script>
