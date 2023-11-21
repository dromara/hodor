<template>
  <component :is="$antIcons[type]" />
</template>
<script>
export default {
    name: "p-icon",
    props: {
        type: {
            type: String,
            required: true
        }
    }
}
</script>
