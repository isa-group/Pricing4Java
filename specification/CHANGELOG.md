# Yaml4SaaS Specification Changelog

:warning: If no `version` is specified version 1.0 is set by default

## Version 1.0

## Version 1.1

### :white_check_mark: New

- new field `createdAt` to replace `day` `month` and `year`, see this [section](#deleted)
- now addOns can depend on addOns putting the name of the addOn in the `avaliableFor` field

### :x: Deleted

- `day` `month` and `year` were used to indicate the day when the pricing was modeled.
  These fields has been deleted as in yaml it is possible to specify a date.
  Use new field `createdAt` instead.

### :up: Improvements

- Now you can skip duplicating the same `expression` in the `serverExpression` in a feature.
  `serverExpression` has the same evaluation in `expression` by default. You can still
  overwrite `serverExpression` if you want to be different.

## Version 2.0

### :white_check_mark: New

- new field in plan called `price` that supports setting the price with a number or
  compute mathematical expression to determine the price of the plan. Expression can
  be greatly reduced with `variables` feature
- new root field called `variables` to use in conjunction when computing expressions
  plans prices. Assume that your plan price is calculated according to the number of
  conections `c` you want and uses the following formula `2*c + 3 / (c+1)` to compute
  the price. You can define in `variables` map parameter `c`.
- dependencies between addons using the attribute `dependsOn`

### :x: Deleted

- plan fields `monthlyPrice` and `annualPrice` removed, use `price` instead

## Future Work (not yet implemented)

- new field in plan called `private` to indicate denote that you should contact sales
  to hire their services

- new root field called `billing` holds the discount ratio of different billing
  present in the pricing. Assumming that discount ratios are applied to the monthly price
  of plans and addOns

- feature groups using tags
