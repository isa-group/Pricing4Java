# Proposal

## Pricing with formula

### Migration from 1.1 to 2.0

Migrate from `annualPrice` and `monthlyPrice` (1.1) to price
(2.0)

If annualPrice or monthlyPrices holds a ``#`` should be interpreted
as a expression


```yaml
---
# Se pasa directamente en el actualizador al campo price y en el parser se setea
annualPrice: null
monthlyPrice: 0.0
---
# lanzar un warning en el actualizador, cuidado que 
annualPrice: 15.99
monthlyPrice: null
---
# postman
# Pasas directamente la cadena tal cual, en el parser se pone la descripcion del precio
plans:
  ENTERPRISE_ULTIMATE:
    description: ""
    monthlyPrice: SALES
    annualPrice: null
---
# Pasas directamente la cadena tal cual, en el parser detecta que hay # y se tiene que evaluar
plans:
  ENTERPRISE_ULTIMATE:
    description: ""
    monthlyPrice: "#x + #y"
    annualPrice: null
---
# monday
plans:
  ENTERPRISE:
    description: "Get exclusive features for your organization"
    monthlyPrice: Contact sales
    annualPrice: Contact sales
---
# Tiene que lanzar error en el actualizador
monthlyPrice: null
annualPrice: null
---
# rapidAPI
plans:
  ENTERPRISE:
    description: "Bring software to market faster (Annual billing only)"
    monthlyPrice: Custom Pricing
    annualPrice: null
---
# wrike
plans:
  ENTERPRISE:
    monthlyPrice: Contact Sales
    annualPrice: Contact Sales
  PINNACLE:
    monthlyPrice: Contact sales
    annualPrice: Contact sales
addOns:
  wrikeIntegrate:
    availableFor:
      - BUSINESS
      - ENTERPRISE
      - PINNACLE
    price: Contact Sales
  wrikeSync:
    availableFor:
      - BUSINESS
      - ENTERPRISE
      - PINNACLE
    price: Contact Sales
  wrikeLock:
    availableFor:
      - ENTERPRISE
      - PINNACLE
    price: Contact Sales
```

``price`` could be either a string or a real number

```yaml
plan:
  PRO:
    price: 6*x + 2
variables:
  x: 4
```

## References
- https://zapier.com/pricing

## Custom billing

Depending on the button that clicks the user, it
displays different prices

|--------|----------|------------|
| Yearly |  Monthly | Six months |
|--------|----------|------------|
```yaml
billing:
  monthly: 1
  3-month: 0.98
  6-month: 0.95
  yearly: 0.9
plans:
  PRO:
    price: 30
    useMultipleBilling: true
```

### Migrate from version 1.1 to version 2.0

We assume that we have montly billing, then the discount factor
of diferent custom billing prices will be applied to the monthly
price.

Version 1.1:

```yaml
monthlyPrice: 14.99
annualPrice: 13.49
```

Version 2.0:

We can only derive the annual discount factor only if:

``monthlyPrice`` and `annualPrice` are set, the discount
factor will be ``annualPrice / monthlyPrice``

Version 2.0 example billing:

```yaml
plans:
  billing:
    monthly: 1
    3-month: 0.98
    6-month: 0.95
    yearly: 0.9
  plans:
    BASIC:
      price: 30
    PRO: 
      price: 60
    ADVANCED:
      price: 90
```

## Templates

```yaml
features:
	feature1:
		description: Description 1
		extends: builtin
	feature2:
		description: Description 2
		extends: builtin
usageLimits:
	usageLimit1:
		description: Usage limit 1
		extends: off
templates:
	builtin:
		type: DOMAIN
		useTemplate: on
	on:
		valueType: BOOLEAN
		defaultValue: true
	off:
		valueType: BOOLEAN
		defaultValue: false
```

## Bundles

```yaml
plans:
  BASIC:
  # ...
  PRO:
  # ...
  PREMIUM:
  # ---
addOns:
  A:
  # ...
  B:
  # ...
  C:
  # ...
bundles:
  bundle1:
    - BASIC
    - A
  bundle2:
    - PRO
    - A
    - B
  bundle3:
    - PREMIUM
    - A
    - B
    - C
```
