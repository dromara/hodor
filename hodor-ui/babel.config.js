module.exports = {
  presets: [
    "@vue/cli-plugin-babel/preset",
    [
      "@babel/preset-env",
      {
        useBuiltIns: "entry",
        corejs: 3
      }
    ]
  ],
  plugins: [
    [
      "import",
      {
        libraryName: "ant-design-vue",
        libraryDirectory: "es",
        style: true // `style: true` 会加载 less 文件
      }
    ]
  ]
};