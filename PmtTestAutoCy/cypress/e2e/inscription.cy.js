describe('test1', () => {
  it('create account', () => {
    cy.visit('http://localhost:4200/')
    cy.get('a').contains('inscrire').click()
    cy.get('#username').type('testMouheb1')
    cy.get('#email').type('testMouheb1@test.com')
    cy.get('#password').type('test')
    cy.get('#confirmPassword').type('test')
    cy.get('button').contains('inscrire').click()

  })
})